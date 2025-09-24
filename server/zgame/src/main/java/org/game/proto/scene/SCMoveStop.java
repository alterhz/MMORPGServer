package org.game.proto.scene;

import org.game.core.message.Proto;

@Proto(value = 50008)
public class SCMoveStop {

    private long unitId;

    private Position position;

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long value) {
        this.unitId = value;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position value) {
        this.position = value;
    }
}
