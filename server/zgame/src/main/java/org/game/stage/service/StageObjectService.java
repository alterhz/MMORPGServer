package org.game.stage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.stage.StageObject;
import org.game.stage.rpc.IStageObjectService;

public class StageObjectService extends GameServiceBase implements IStageObjectService {

    public static final Logger logger = LogManager.getLogger(StageObjectService.class);

    private final StageObject stageObject;

    public StageObjectService(String id, StageObject stageObject) {
        super(id);
        this.stageObject = stageObject;
    }

    @Override
    public void init() {
        logger.info("StageObjectService 初始化. {}", stageObject);
    }

    @Override
    public void startup() {
        logger.info("StageObjectService 启动");
    }

    @Override
    public void destroy() {
        logger.info("StageObjectService 销毁");
    }

    @Override
    public void hotfix(Param param) {
        logger.info("StageObjectService 热修复: param={}", param);
    }
}