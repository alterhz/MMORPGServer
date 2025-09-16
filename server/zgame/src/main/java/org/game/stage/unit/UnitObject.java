package org.game.stage.unit;

import org.game.stage.StageObject;

public abstract class UnitObject {

    private final long unitId;

    private final StageObject stageObj;

    public UnitObject(long unitId, StageObject stageObj) {
        this.unitId = unitId;
        this.stageObj = stageObj;
    }

    public long getUnitId() {
        return unitId;
    }

    public StageObject getStageObj() {
        return stageObj;
    }


}
