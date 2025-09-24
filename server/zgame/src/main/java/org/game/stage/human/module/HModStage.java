package org.game.stage.human.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.event.EventListener;
import org.game.core.message.ProtoListener;
import org.game.core.rpc.ReferenceFactory;
import org.game.player.rpc.IPlayerInfoService;
import org.game.proto.scene.*;
import org.game.stage.StageObject;
import org.game.stage.entity.module.UModMove;
import org.game.stage.human.HumanModBase;
import org.game.stage.human.HumanObject;
import org.game.core.utils.Vector3;
import org.game.stage.human.event.OnStageReadyEvent;
import org.game.stage.module.SModUnits;

import java.util.List;

public class HModStage extends HumanModBase {

    public static final Logger logger = LogManager.getLogger(HModStage.class);


    public HModStage(HumanObject stageHumanObj) {
        super(stageHumanObj);
    }

    @ProtoListener(EnterStageRequest.class)
    public void onEnterStage(EnterStageRequest enterStageRequest) {
        HumanObject humanObj = getHumanObj();
        StageObject stageObj = humanObj.getStageObj();
        stageObj.enterStage(humanObj);
    }

    @ProtoListener(UnitMoveRequest.class)
    public void onUnitMove(UnitMoveRequest moveRequest) {
        HumanObject humanObj = getHumanObj();

        // 创建目标位置
        Vector3 targetPosition = new Vector3(
                (float) moveRequest.getX(),
                (float) moveRequest.getY(),
                (float) moveRequest.getZ()
        );

        // 获取移动模块
        UModMove moveMod = humanObj.getUMod(UModMove.class);
        if (moveMod != null) {
            // 设置移动到目标点
            moveMod.moveTo(targetPosition);

            // 发送移动响应
            UnitMoveResponse response = new UnitMoveResponse();
            response.setFix(false); // 不需要修正位置

            Position pos = new Position();
            pos.setX(moveRequest.getX());
            pos.setY(moveRequest.getY());
            pos.setZ(moveRequest.getZ());
            response.setPosition(pos);

            humanObj.sendMessage(response);
        }
    }

    @EventListener
    public void onStageReady(OnStageReadyEvent event) {
        HumanObject humanObj = getHumanObj();
        logger.info("HModStage.onStageReady: humanId={}", humanObj.getEntityId());
    }

    public void leaveStage() {
        HumanObject humanObj = getHumanObj();
        StageObject stageObj = humanObj.getStageObj();
        if (stageObj == null) {
            logger.error("HModStage.leaveStage: stageObj is null");
            return;
        }

        // TODO test
        List<HumanObject> humanObjects = stageObj.getMod(SModUnits.class).getHumanObjects();
        logger.info("human count： {}", humanObjects.size());

        savePosition();

        stageObj.leaveStage(humanObj);
    }

    private void savePosition() {
        HumanObject humanObj = getHumanObj();
        StageObject stageObj = humanObj.getStageObj();
        if (stageObj == null) {
            logger.error("HModStage.savePosition: stageObj is null");
            return;
        }

        Vector3 position = new Vector3(1, 2, 3);
        IPlayerInfoService playerInfoService = ReferenceFactory.getPlayerProxy(IPlayerInfoService.class, humanObj.getPlayerId());
        playerInfoService.savePosition(position);
    }

}