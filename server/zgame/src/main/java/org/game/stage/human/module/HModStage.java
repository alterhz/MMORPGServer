package org.game.stage.human.module;

import org.game.core.message.ProtoListener;
import org.game.proto.scene.EnterStageRequest;
import org.game.stage.StageObject;
import org.game.stage.human.HumanObject;

public class HModStage extends HumanModBase {
    public HModStage(HumanObject stageHumanObj) {
        super(stageHumanObj);
    }

    @ProtoListener(EnterStageRequest.class)
    public void onEnterStage(EnterStageRequest enterStageRequest) {
        HumanObject stageHumanObj = getStageHumanObj();
        StageObject stageObj = stageHumanObj.getStageObj();
        stageObj.humanEnter(stageHumanObj);
    }
}
