package org.game.proto.scene;

import java.util.List;
import org.game.core.message.Proto;

@Proto(value = 50007)
public class UnitMoveBroadcast {

    private long unitId;

    private List<Position> position;

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long value) {
        this.unitId = value;
    }

    public List<Position> getPosition() {
        return position;
    }

    public void setPosition(List<Position> value) {
        this.position = value;
    }
}
