package org.game.stage.unit;

import org.game.stage.StageObject;

public class StageHumanObject extends UnitObject {

    private final String humanId;

    public StageHumanObject(StageObject stageObject, long unitId, String humanId) {
        super(unitId, stageObject);
        this.humanId = humanId;
    }

    public String getHumanId() {
        return humanId;
    }

    // TODO 添加协议监听

    // TODO 添加事件监听
}
