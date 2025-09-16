package org.game.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.RPCProxy;


public abstract class GameServiceBase {

    public static final Logger logger = LogManager.getLogger(GameServiceBase.class);


    /**
     * 服务名称
     */
    private final String name;

    /**
     * 定时器队列
     */
    protected final TimerQueue timerQueue = new TimerQueue();

    private final TickTimer pulseSecondTimer = new TickTimer(1000);

    /**
     * 当前服务所属的线程
     */
    protected GameThread gameThread;

    // 其他基础服务方法
    public GameServiceBase(String name) {
        this.name = name;
    }

    public void bindGameThread(GameThread gameThread) {
        this.gameThread = gameThread;
    }

    public void registerServiceMap() {
        Class<?> clazz = this.getClass();

        // 1. 获取包含RPCProxy注解的接口
        // 2. 获取接口名称（不包含包名）作为服务名称
        Class<?> targetInterface = null;
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            // 判断包含注解@RPCProxy
            RPCProxy rpcProxy = interfaceClass.getAnnotation(RPCProxy.class);
            if (rpcProxy != null) {
                targetInterface = interfaceClass;
                break;
            }
        }

        if (targetInterface == null) {
            logger.error("No RPCProxy annotation found on interface: {}", clazz.getName());
            throw new RuntimeException("No RPCProxy annotation found on interface: " + clazz.getName());
        }

        // 获取服务名称，如果注解中未指定，则使用接口名称（不包含包名）
        String serviceName = targetInterface.getSimpleName().toLowerCase();

        // 使用 ServiceRegistryManager 注册服务
        if (!ServiceRegistryManager.registerService(serviceName, gameThread.getName())) {
            throw new RuntimeException("Service already registered: " + serviceName);
        }
        logger.info("Service registered: {} on thread: {}", serviceName, gameThread.getName());
    }

    // 初始化
    public abstract void init();

    // 启动
    public abstract void startup();

    // 心跳
    public final void pulse(long now) {
        if (pulseSecondTimer.update(now)) {
            onPulseSec(now);
        }
    }

    protected void onPulse(long now) {

    }

    protected void onPulseSec(long now) {

    }

    // 销毁
    public abstract void destroy();

    // 获取服务名称
    public String getName() {
        return name;
    }

    // 计算服务应分配的线程索引
    public int getThreadIndex(int threadCount) {
        return Math.abs(name.hashCode()) % threadCount;
    }

    public GameThread getGameThread() {
        return gameThread;
    }

}
