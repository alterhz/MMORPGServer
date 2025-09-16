package org.game.stage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.stage.rpc.IStageService;

import java.util.concurrent.CompletableFuture;

public class StageService extends GameServiceBase implements IStageService {

    public static final Logger logger = LogManager.getLogger(StageService.class);

    public static final String NAME = "StageService";

    public StageService() {
        super(NAME);
    }

    @Override
    public void init() {
        logger.info("StageService 初始化");
    }

    @Override
    public void startup() {
        logger.info("StageService 启动");
    }

    @Override
    public void destroy() {
        logger.info("StageService 销毁");
    }

    @Override
    public void hotfix(Param param) {
        logger.info("StageService 热修复: param={}", param);
    }

    @Override
    public CompletableFuture<Param> createCommonStage(int stageSn) {
        logger.info("创建普通场景: stageSn={}", stageSn);

        // TODO 创建StageObjectService

        // TODO 创建StageObject

        // 调用全局服务创建场景，使用人数最少的分配策略
        return CompletableFuture.completedFuture(new Param()); // ALLOC_TYPE_MIN_HUMAN = 1
    }
}