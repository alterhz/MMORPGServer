package org.game.stage.human.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.event.EventListener;
import org.game.core.message.ProtoListener;
import org.game.proto.scene.EnterStageRequest;
import org.game.stage.StageObject;
import org.game.stage.human.HumanObject;
import org.game.stage.human.event.OnStageReadyEvent;

public class HModStage extends HumanModBase {

    public static final Logger logger = LogManager.getLogger(HModStage.class);


    public HModStage(HumanObject stageHumanObj) {
        super(stageHumanObj);
    }

    @ProtoListener(EnterStageRequest.class)
    public void onEnterStage(EnterStageRequest enterStageRequest) {
        HumanObject humanObj = getHumanObj();
        StageObject stageObj = humanObj.getStageObj();
        stageObj.humanEnter(humanObj);
    }

    @EventListener
    public void onStageReady(OnStageReadyEvent event) {
        HumanObject humanObj = getHumanObj();
        logger.info("HModStage.onStageReady: humanId={}", humanObj.getHumanId());
    }

}
