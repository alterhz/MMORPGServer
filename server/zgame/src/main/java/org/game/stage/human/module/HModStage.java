package org.game.stage.human.module;

import org.game.core.message.ProtoListener;
import org.game.proto.scene.CSEnterScene;
import org.game.stage.StageObject;
import org.game.stage.human.HumanObject;

public class HModStage extends HumanModBase {
    public HModStage(HumanObject stageHumanObj) {
        super(stageHumanObj);
    }

    @ProtoListener(CSEnterScene.class)
    public void onEnterScene(CSEnterScene csEnterScene) {
        HumanObject stageHumanObj = getStageHumanObj();
        StageObject stageObj = stageHumanObj.getStageObj();
        stageObj.humanEnter(stageHumanObj);
    }
}
