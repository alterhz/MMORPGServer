package org.game.stage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.net.Message;
import org.game.core.utils.SnowflakeIdGenerator;
import org.game.stage.StageHumanData;
import org.game.stage.StageObject;
import org.game.stage.rpc.IStageHumanObjectService;
import org.game.stage.unit.StageHumanObject;

import java.util.HashMap;
import java.util.Map;

public class StageHumanObjectService extends GameServiceBase implements IStageHumanObjectService {

    public static final Logger logger = LogManager.getLogger(StageHumanObjectService.class);

    private final StageHumanObject stageHumanObj;
    
    public StageHumanObjectService(String humanId, StageHumanObject stageHumanObj) {
        super(humanId);
        this.stageHumanObj = stageHumanObj;
    }

    public StageHumanObject getStageHumanObj() {
        return stageHumanObj;
    }

    @Override
    public void init() {
        logger.info("StageHumanObjectService 初始化. stageId={}", getName());
    }

    @Override
    public void startup() {
        logger.info("StageHumanObjectService 启动. stageId={}", getName());
    }

    @Override
    public void destroy() {
        logger.info("StageHumanObjectService 销毁. stageId={}", getName());
    }

    @Override
    public void hotfix(Param param) {
        logger.info("StageHumanObjectService 热修复: param={}, stageId={}", param, getName());
    }

    @Override
    public void dispatchProto(Message message) {

    }

}