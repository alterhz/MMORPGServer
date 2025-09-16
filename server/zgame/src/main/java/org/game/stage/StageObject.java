package org.game.stage;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.utils.SnowflakeIdGenerator;
import org.game.global.rpc.IStageGlobalService;
import org.game.stage.unit.StageHumanObject;
import org.game.stage.unit.UnitObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 场景对象
 */
public class StageObject {

    public static final Logger logger = LogManager.getLogger(StageObject.class);

    private final int stageSn;
    private final long stageId;

    private final Map<Long, UnitObject> stageUnits = new HashMap<>();

    private final Map<String, StageHumanObject> prepareEnterStageHumans = new HashMap<>();

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

    public void humanEnter(StageHumanObject stageHumanObj) {
        if (stageUnits.containsKey(stageHumanObj.getUnitId())) {
            logger.error("stageHumanObj already exist. stageHumanObj: {}", stageHumanObj);
            return;
        }

        stageUnits.put(stageHumanObj.getUnitId(), stageHumanObj);

        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);
        stageGlobalService.humanEnter(stageId);
    }

    public void humanLeave(StageHumanObject stageHumanObj) {
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
