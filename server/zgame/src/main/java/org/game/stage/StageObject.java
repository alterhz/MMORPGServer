package org.game.stage;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 场景对象
 */
public class StageObject {

    private final int stageSn;
    private final long stageId;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("stageSn", stageSn)
                .append("stageId", stageId)
                .toString();
    }
}
