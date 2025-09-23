package org.game.stage.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.stage.StageObject;
import org.game.stage.human.HumanObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SModUnits extends StageModBase {

    private final Map<Long, HumanObject> humanMap = new HashMap<>();

    public SModUnits(StageObject stageObj) {
        super(stageObj);
    }

    public void onUnitEnter(HumanObject humanObject) {
        humanMap.put(humanObject.getUnitId(), humanObject);
    }

    public void onUnitLeave(HumanObject humanObject) {
        humanMap.remove(humanObject.getUnitId());
    }

    // 获取角色列表
    public List<HumanObject> getHumanObjects() {
        return new ArrayList<>(humanMap.values());
    }

    public HumanObject getHumanObj(long unitId) {
        return humanMap.get(unitId);
    }
}
