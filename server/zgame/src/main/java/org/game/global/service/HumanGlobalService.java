package org.game.global.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.TickTimer;
import org.game.global.rpc.IHumanGlobalService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HumanGlobalService extends GameServiceBase implements IHumanGlobalService {

    public static final Logger logger = LogManager.getLogger(HumanGlobalService.class);
    
    // 在线玩家计数器
    private final AtomicInteger onlineCount = new AtomicInteger(0);
    
    // 在线玩家列表
    private final ConcurrentHashMap<Long, String> onlinePlayers = new ConcurrentHashMap<>();
    
    // 添加TickTimer，每5秒执行一次
    private final TickTimer tickTimer = new TickTimer(5000);

    private int rpcCallCount = 0;

    public HumanGlobalService(String name) {
        super(name);
    }

    // 实现基本方法
    @Override
    public void init() {
        // 初始化逻辑
        logger.info("HumanGlobalService 初始化");
    }

    @Override
    public void startup() {
        logger.info("HumanGlobalService 启动");

    }

    @Override
    public void onPulse(long now) {
        // 使用TickTimer执行定时任务
        if (tickTimer.update(System.currentTimeMillis())) {
            // 每隔5秒执行一次的逻辑
            logger.debug("TickTimer触发，在线人数: {}", onlineCount.get());
        }
    }

    @Override
    public void destroy() {
        // 销毁逻辑
        logger.info("HumanGlobalService 销毁");
        onlinePlayers.clear();
        onlineCount.set(0);
    }

    @Override
    public CompletableFuture<Integer> getHumanOnlineCount(int minLevel) {
        return CompletableFuture.completedFuture(onlineCount.get() + 101);
    }

    @Override
    public void test() {
        ++rpcCallCount;
    }

    @Override
    public void hotfix(Param param) {
        logger.info("HumanGlobalService 热修复: param={}", param);
    }
}