package org.game.stage;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.ReferenceFactory;
import org.game.global.rpc.IStageGlobalService;
import org.game.stage.human.HumanObject;
import org.game.stage.unit.UnitObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景对象
 */
public class StageObject {

    public static final Logger logger = LogManager.getLogger(StageObject.class);

    private final int stageSn;
    private final long stageId;

    private final Map<Long, UnitObject> stageUnits = new HashMap<>();

    private final Map<String, HumanObject> prepareEnterStageHumans = new HashMap<>();

    public StageObject(int stageSn, long stageId) {
        this.stageSn = stageSn;
        this.stageId = stageId;
    }

    public int getStageSn() {
        return stageSn;
    }

    public long getStageId() {
        return stageId;
    }

    protected void onPulse(long now) {

    }

    protected void onPulseSec(long now) {

    }

    public void pulse(long now) {
        // 复制stageUnits，然后心跳
        List<UnitObject> stageUnits = new ArrayList<>(this.stageUnits.values());

        for (UnitObject stageUnit : stageUnits) {
            stageUnit.onPulse(now);
        }

        onPulse(now);
    }

    public void pulseSec(long now) {
        // 复制stageUnits，然后心跳
        List<UnitObject> stageUnits = new ArrayList<>(this.stageUnits.values());

        for (UnitObject stageUnit : stageUnits) {
            stageUnit.onPulseSec(now);
        }

        onPulseSec(now);
    }

    public void humanEnter(HumanObject stageHumanObj) {
        if (stageUnits.containsKey(stageHumanObj.getUnitId())) {
            logger.error("stageHumanObj already exist. stageHumanObj: {}", stageHumanObj);
            return;
        }

        stageUnits.put(stageHumanObj.getUnitId(), stageHumanObj);

        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);
        stageGlobalService.humanEnter(stageId);
    }

    public void humanLeave(HumanObject stageHumanObj) {
        if (!stageUnits.containsKey(stageHumanObj.getUnitId())) {
            logger.error("stageHumanObj not exist. stageHumanObj: {}", stageHumanObj);
            return;
        }

        stageUnits.remove(stageHumanObj.getUnitId());

        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);
        stageGlobalService.humanLeave(stageId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("stageSn", stageSn)
                .append("stageId", stageId)
                .toString();
    }
}
