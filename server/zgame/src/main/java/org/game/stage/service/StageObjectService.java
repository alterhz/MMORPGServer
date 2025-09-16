package org.game.stage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.GameThread;
import org.game.core.Param;
import org.game.core.utils.SnowflakeIdGenerator;
import org.game.stage.StageHumanData;
import org.game.stage.StageObject;
import org.game.stage.rpc.IStageObjectService;
import org.game.stage.unit.StageHumanObject;

public class StageObjectService extends GameServiceBase implements IStageObjectService {

    public static final Logger logger = LogManager.getLogger(StageObjectService.class);

    private final StageObject stageObj;

    public StageObjectService(String id, StageObject stageObj) {
        super(id);
        this.stageObj = stageObj;
    }

    @Override
    public void init() {
        logger.info("StageObjectService 初始化. {}", stageObj);
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

    @Override
    public void registerStageHuman(StageHumanData stageHumanData) {
        GameThread currentGameThread = GameThread.getCurrentGameThread();
        if (currentGameThread.getGameService(stageHumanData.getHumanId()) != null) {
            logger.error("角色已经在当前线程: {}。humanId={}", currentGameThread.getName(), stageHumanData.getHumanId());
        }

        long id = SnowflakeIdGenerator.getInstance().nextId();
        StageHumanObject stageHumanObject = new StageHumanObject(stageObj, id, stageHumanData.getHumanId());
        StageHumanObjectService stageHumanObjectService = new StageHumanObjectService(stageHumanData.getHumanId(), stageHumanObject);

        currentGameThread.addGameService(stageHumanObjectService);
        currentGameThread.runTask(() -> {
            stageHumanObjectService.init();
            stageHumanObjectService.startup();
        });
    }
}