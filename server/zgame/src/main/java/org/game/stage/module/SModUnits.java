package org.game.stage.module;

import org.game.core.event.EventListener;
import org.game.stage.StageObject;
import org.game.stage.event.EnterStageEvent;
import org.game.stage.event.LeaveStageEvent;
import org.game.stage.human.HumanObject;
import org.game.stage.unit.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SModUnits extends StageModBase {

    private final Map<Long, HumanObject> humanMap = new HashMap<>();

    public SModUnits(StageObject stageObj) {
        super(stageObj);
    }

    @EventListener
    public void onUnitEnter(EnterStageEvent enterStageEvent) {
        Entity entity = enterStageEvent.getUnitObject();
        if (entity instanceof HumanObject) {
            humanMap.put(entity.getEntityId(), (HumanObject) entity);
        }
    }

    @EventListener
    public void onUnitLeave(LeaveStageEvent leaveStageEvent) {
        Entity entity = leaveStageEvent.getUnitObject();
        if (entity instanceof HumanObject) {
            humanMap.remove(entity.getEntityId());
        }
    }

    // 获取角色列表
    public List<HumanObject> getHumanObjects() {
        return new ArrayList<>(humanMap.values());
    }

    public HumanObject getHumanObj(long unitId) {
        return humanMap.get(unitId);
    }
}
