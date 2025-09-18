package org.game.proto.scene;

import org.game.core.message.Proto;

@Proto(value = 50006)
public class UnitMoveResponse {

    private boolean fix;

    private Position position;

    public boolean getFix() {
        return fix;
    }

    public void setFix(boolean value) {
        this.fix = value;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position value) {
        this.position = value;
    }
}
