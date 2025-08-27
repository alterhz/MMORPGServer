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

            testRPCCost();

        }, new Param().put("name", "张三"));

        timerQueue.delay(60000, (id, context) -> {
            timerQueue.cancelTimer(timerId);
        });

        testRPC();
    }

    private void testRPC() {
        // 纳秒计算
        long start = System.nanoTime();
        // 获取服务代理
        IHumanGlobalService humanGlobalService = ReferenceFactory.getProxy(IHumanGlobalService.class);

        // 异步调用获取在线人数
        final CompletableFuture<Integer> future = humanGlobalService.getHumanOnlineCount(1);

        // 耗时结束
        long end = System.nanoTime();
        logger.info("RPC单次调用，耗时（毫秒）: {} ", String.format("%.3f", (end - start) / 1000000.0f));

        future.whenComplete((count, throwable) -> {
            if (throwable != null) {
                logger.error("获取在线人数失败", throwable);
            } else {
                // 异步处理结果
                logger.info("在线人数 = {}", count);
            }
        });
    }

    private void testRPCCost() {
        // 纳秒计算
        long start = System.nanoTime();

        for (int i = 0; i < 10000; i++) {
            IHumanGlobalService humanGlobalService = ReferenceFactory.getProxy(IHumanGlobalService.class);

            // 异步调用获取在线人数
            humanGlobalService.test();
        }

        // 耗时结束
        long end = System.nanoTime();
        // 打印耗时，毫秒，小数点后3位
        logger.info("1万次RPC调用，耗时（毫秒）: {}", String.format("%.3f", (end - start) / 1000000.0f));
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