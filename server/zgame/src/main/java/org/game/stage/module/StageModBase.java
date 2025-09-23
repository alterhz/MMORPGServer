package org.game.stage.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.stage.StageObject;
import org.game.stage.unit.Entity;

public class StageModBase {

    public static final Logger logger = LogManager.getLogger(StageModBase.class);

    private final StageObject stageObj;

    public StageModBase(StageObject stageObj) {
        this.stageObj = stageObj;
    }

    public StageObject getStageObj() {
        return stageObj;
    }

    public <T extends StageModBase> T getMod(Class<T> clazz) {
        return stageObj.getMod(clazz);
    }

    public void onPulse(long now) {

    }

    public void onPulseSec(long now) {

    }
}