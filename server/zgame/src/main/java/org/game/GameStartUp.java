package org.game;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.game.config.MyConfig;
import org.game.core.GameProcess;
import org.game.core.GameServiceBase;
import org.game.core.GameThread;
import org.game.core.db.DaoScanner;
import org.game.core.db.HumanDBManager;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.db.MongoDBSyncClient;
import org.game.core.event.HumanEventDispatcher;
import org.game.core.human.HumanProtoDispatcher;
import org.game.core.net.NettyServer;
import org.game.core.rpc.RPCProxy;
import org.game.core.stage.StageThread;
import org.game.core.utils.ScanClassUtils;
import org.game.core.human.HumanThread;
import org.game.core.message.ProtoScanner;
import org.game.core.utils.SnowflakeIdGenerator;
import org.game.stage.service.StageService;

import java.lang.reflect.Constructor;
import java.util.*;

public class GameStartUp {
    public static void main(String[] args) {

        int serverId = 20001;
        BaseUtils.init(serverId);
        SnowflakeIdGenerator.init(serverId);

        try {
            // 1. 载入配置文件
            MyConfig.load();

            // 初始化Proto
            ProtoScanner.init();

            // HumanProtoListener初始化
            HumanProtoDispatcher.getInstance().init();

            HumanEventDispatcher.getInstance().init();

            // DB实体扫描
            DaoScanner.init();

            // HumanLoader初始化
            HumanDBManager.init();

            // 初始化同步MongoDB
            String mongoDbUri = MyConfig.getConfig().getMongodb().getUri();
            String mongoDbName = MyConfig.getConfig().getMongodb().getDbName();
            MongoDBSyncClient.init(mongoDbUri, mongoDbName);
            // 初始化异步MongoDB
            MongoDBAsyncClient.init(mongoDbUri, mongoDbName);


            // 2. 创建GameProgress
            initGameProcess();

            // 启动连接线程
            NettyServer.startConnThread(MyConfig.getConfig().getConnThread().getCount());

            // 启动HumanThread线程组
            createHumanThreads();

            // 3. 创建GameThread01
            createGameThreads();

            // 创建场景线程
            createStageThreads();

            // 4. 扫描@ServiceConfig注解
            Set<Class<?>> serviceClasses = ScanClassUtils.scanGlobalServiceClasses();

            // 5. 检查服务名称是否重复
            Map<String, Class<?>> serviceMap = validateServiceNames(serviceClasses);

            // 6. 创建GameService实例并调用init方法
            List<GameServiceBase> services = createGameServices(serviceMap);

            // 7. 调用startup方法，分配到对应的GameThread
            assignServicesToThreads(services);

            // 8. 启动网络 服务
            int port = MyConfig.getConfig().getServer().getPort();
            NettyServer server = new NettyServer(port, "your_rc4_key");
            server.start();

            LogCore.logger.info("游戏服务启动成功");

            // 阻塞，直到程序结束
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
            }


            // 8. Hook钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LogCore.logger.info("游戏服务正在关闭...");
            }));
        } catch (Exception e) {
            LogCore.logger.error("游戏服务启动失败", e);
            System.exit(1);
        }
    }

    private static void createHumanThreads() {
        int humanThreadCount = MyConfig.getConfig().getHumanThread().getCount();
        for (int i = 0; i < humanThreadCount; i++) {
            HumanThread humanThread = new HumanThread(i);
            humanThread.start();
        }
    }

    // StageThread 创建
    private static void createStageThreads() {
        int stageThreadCount = MyConfig.getConfig().getStageThread().getCount();
        for (int i = 0; i < stageThreadCount; i++) {
            StageThread stageThread = new StageThread(i);
            stageThread.start();

            stageThread.runTask(() -> {
                // 添加StageService服务，每个线程一个
                addStageService(stageThread);
            });
        }
    }

    private static void addStageService(StageThread stageThread) {
        StageService stageService = new StageService();
        stageThread.addGameService(stageService);
        stageThread.runTask(() -> {
            stageService.init();
            stageService.startup();
            LogCore.logger.info("stageService={}", stageService);
        });
    }

    /**
     * 创建GameProgress实例
     * @return GameProgress对象
     */
    private static void initGameProcess() {
        String serverId = System.getProperty("serverId", "20001");
        String progressName = MyConfig.getConfig().getServer().getPrefix() + serverId;
        GameProcess.SetGameProcessName(progressName);
    }

    /**
     * 创建指定数量的GameThread实例并启动
     */
    private static void createGameThreads() {
        int threadCount = MyConfig.getConfig().getGameThread().getCount();
        for (int i = 0; i < threadCount; i++) {
            String threadName = "GameThread" + i;
            GameThread gameThread = new GameThread(threadName);
            gameThread.start();
        }
        LogCore.logger.info("创建{}个GameThread成功", threadCount);
    }

    /**
     * 验证服务名称的唯一性
     * @param serviceClasses 服务类集合
     * @return 服务名称到类的映射
     */
    private static Map<String, Class<?>> validateServiceNames(Set<Class<?>> serviceClasses) {
        Map<String, Class<?>> serviceMap = new HashMap<>();
        for (Class<?> clazz : serviceClasses) {
            if (!GameServiceBase.class.isAssignableFrom(clazz)) {
                continue;
            }

            // 1. 获取实现的接口列表
            Class<?>[] interfaces = clazz.getInterfaces();

            if (interfaces.length == 0) {
                continue;
            }
            
            // 2. 判断接口包含注解RPCProxy
            Class<?> targetInterface = null;
            int rpcProxyCount = 0;
            
            for (Class<?> interfaceClass : interfaces) {
                RPCProxy annotation = interfaceClass.getAnnotation(RPCProxy.class);
                if (annotation != null) {
                    targetInterface = interfaceClass;
                    rpcProxyCount++;
                }
            }
            
            // 3. 包含RPCProxy注解数量大于1，抛出异常
            if (rpcProxyCount > 1) {
                LogCore.logger.error("类 {} 包含多个RPCProxy注解", clazz.getName());
                throw new RuntimeException("类 " + clazz.getName() + " 包含多个RPCProxy注解");
            }

            // 检查类是否包含RPCProxy注解并且继承自GameServiceBase
            if (targetInterface == null) {
                continue;
            }

            // 4. 接口名称作为服务名称
            String serviceName = targetInterface.getSimpleName().toLowerCase();
            
            // 5. 判断是否有服务名称重复，有则抛出异常
            if (serviceMap.containsKey(serviceName)) {
                LogCore.logger.error("发现重复的GameService名称: {}", serviceName);
                throw new RuntimeException("GameService名称重复: " + serviceName);
            }
            
            // 6. 保存服务名称和实现类
            serviceMap.put(serviceName, clazz);
        }
        return serviceMap;
    }

    /**
     * 初始化所有GameService实例
     * @param serviceMap 服务名称到类的映射
     * @return 初始化后的GameService实例列表
     */
    private static List<GameServiceBase> createGameServices(Map<String, Class<?>> serviceMap) {
        List<GameServiceBase> services = new ArrayList<>();
        for (Map.Entry<String, Class<?>> entry : serviceMap.entrySet()) {
            String serviceName = entry.getKey();
            Class<?> serviceClass = entry.getValue();

            // 获取接口中包含RPCProxy的注解，仅org.game.core.rpc.RPCProxy.startupType=DEFAULT的服务
            boolean shouldCreateService = false;
            for (Class<?> interfaceClass : serviceClass.getInterfaces()) {
                RPCProxy rpcProxy = interfaceClass.getAnnotation(RPCProxy.class);
                if (rpcProxy != null && rpcProxy.startupType() == RPCProxy.StartupType.DEFAULT) {
                    shouldCreateService = true;
                    break;
                }
            }
            
            if (!shouldCreateService) {
                continue;
            }

            try {
                Constructor<?> constructor = serviceClass.getConstructor(String.class);
                GameServiceBase service = (GameServiceBase) constructor.newInstance(serviceName);

                // 初始化
                services.add(service);
            } catch (Exception e) {
                LogCore.logger.error("创建GameService失败: {}", serviceName, e);
                throw new RuntimeException("创建GameService失败: " + serviceName, e);
            }
        }
        return services;
    }

    /**
     * 将服务分配到对应的GameThread
     * @param services 需要分配的服务列表
     */
    private static void assignServicesToThreads(List<GameServiceBase> services) {
        int progressThreadCount = GameProcess.getThreadCount("GameThread");
        for (GameServiceBase service : services) {
            // 根据服务名称的hash值分配线程
            int threadIndex = service.getThreadIndex(progressThreadCount);
            String threadName = "GameThread" + threadIndex;
            GameThread targetThread = GameProcess.getGameThread(threadName);
            if (targetThread == null) {
                LogCore.logger.error("无法分配GameService到GameThread: {}, threadName: {}", service.getName(), threadName);
                throw new RuntimeException("无法分配GameService到GameThread: " + service.getName() + ", threadName: " + threadName);
            }

            // 将服务添加到对应线程
            targetThread.addGameService(service);

            // 异步执行启动方法
            targetThread.runTask(() -> {
                try {
                    service.registerServiceMap();
                    service.init();
                    service.startup();
                    LogCore.logger.info("GameService初始化成功: {} 分配到线程: {}", service.getName(), targetThread.getName());
                } catch (Exception e) {
                    LogCore.logger.error("GameService初始化失败: {}", service.getName(), e);
                }
            });
        }
    }
}