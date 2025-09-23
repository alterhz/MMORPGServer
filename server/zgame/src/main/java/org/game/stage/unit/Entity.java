package org.game.stage.unit;

import org.game.stage.StageObject;

public abstract class Entity {

    protected final long entityId;

    protected final StageObject stageObj;

    public Entity(long entityId, StageObject stageObj) {
        this.entityId = entityId;
        this.stageObj = stageObj;
    }

    public long getEntityId() {
        return entityId;
    }

    public StageObject getStageObj() {
        return stageObj;
    }

    public void onEnterStage(StageObject stageObj) {

    }

    public void onLeaveStage(StageObject stageObj) {

    }

    public void onPulse(long now) {

    }

    public void onPulseSec(long now) {

    }
}
