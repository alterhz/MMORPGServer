package org.game.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.rpc.ReferenceFactory;
import org.game.rpc.IHumanGlobalService;
import org.game.rpc.IStageGlobalService;

import java.util.concurrent.CompletableFuture;

public class StageGlobalService extends GameServiceBase implements IStageGlobalService {

    public static final Logger logger = LogManager.getLogger(StageGlobalService.class);

    public StageGlobalService(String name) {
        super(name);
    }

    @Override
    public void init() {
        logger.info("StageGlobalService 初始化");
    }

    @Override
    public void startup() {
        logger.info("StageGlobalService 启动");

        long timerId = timerQueue.createTimer(1000, 3000, (id, context) -> {
            logger.info("定时任务. thread={}, timerId={}, name={}", Thread.currentThread().getName(), id, context.getString("name"));
        }, new Param().put("name", "张三"));

        timerQueue.delay(10000, (id, context) -> {
            timerQueue.cancelTimer(timerId);
        });

        // 获取服务代理
        IHumanGlobalService humanGlobalService = ReferenceFactory.getProxy(IHumanGlobalService.class);

        // 异步调用获取在线人数
        final CompletableFuture<Integer> future = humanGlobalService.getHumanOnlineCount(1);
        future.whenComplete((count, throwable) -> {
            if (throwable != null) {
                logger.error("获取在线人数失败", throwable);
            } else {
                // 异步处理结果
                logger.info("在线人数 = {}", count);
            }
        });
    }

    @Override
    public void pulse(long now) {
        // 可用于定期处理场景逻辑
    }

    @Override
    public void destroy() {
        logger.info("StageGlobalService 销毁");
    }

    @Override
    public void hotfix(Param param) {
        logger.info("StageGlobalService 热修复: param={}", param);
    }
}