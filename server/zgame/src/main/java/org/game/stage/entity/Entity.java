package org.game.stage.entity;

import org.game.core.utils.Vector3;
import org.game.stage.StageObject;

public abstract class Entity {

    protected final long entityId;

    protected final StageObject stageObj;

    protected Vector3 position = new Vector3();

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

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public abstract void onEnterStage(StageObject stageObj);

    public abstract void onLeaveStage(StageObject stageObj);

    public void onPulse(long now) {

    }

    public void onPulseSec(long now) {

    }
}