package org.game.stage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameServiceBase;
import org.game.core.GameThread;
import org.game.core.Param;
import org.game.core.rpc.ToPoint;
import org.game.stage.human.HumanObjectData;
import org.game.stage.StageObject;
import org.game.stage.rpc.IStageObjectService;
import org.game.stage.human.HumanObject;

import java.util.concurrent.CompletableFuture;

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
    protected void onPulseSec(long now) {
        super.onPulseSec(now);

        stageObj.pulseSec(now);
    }

    @Override
    protected void onPulse(long now) {
        super.onPulse(now);

        stageObj.pulse(now);
    }

    @Override
    public CompletableFuture<ToPoint> registerStageHuman(HumanObjectData humanObjectData) {
        GameThread currentGameThread = GameThread.getCurrentGameThread();
        if (currentGameThread.getGameService(String.valueOf(humanObjectData.getPlayerId())) != null) {
            logger.error("角色已经在当前线程: {}。humanId={}", currentGameThread.getName(), humanObjectData.getPlayerId());
        }

        // HumanPoint
        ToPoint humanPoint = new ToPoint(GameProcess.getName(), currentGameThread.getName(), String.valueOf(humanObjectData.getPlayerId()));

        HumanObject humanObj = new HumanObject(stageObj, humanObjectData.getPlayerId());
        humanObj.setClientPoint(humanObjectData.getClientPoint());
        humanObj.setHumanPoint(humanPoint);
        HumanService stageHumanObjectService = new HumanService(humanObj);

        currentGameThread.addGameService(stageHumanObjectService);
        currentGameThread.runTask(() -> {
            stageHumanObjectService.init();
            stageHumanObjectService.startup();
        });

        return CompletableFuture.completedFuture(humanPoint);
    }
}