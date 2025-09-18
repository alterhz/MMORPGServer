package org.game.proto.scene;

import org.game.core.message.Proto;

@Proto(value = 50002)
public class SCEnterScene {

    private long stageSn;

    private long x;

    private long y;

    public long getStageSn() {
        return stageSn;
    }

    public void setStageSn(long value) {
        this.stageSn = value;
    }

    public long getX() {
        return x;
    }

    public void setX(long value) {
        this.x = value;
    }

    public long getY() {
        return y;
    }

    public void setY(long value) {
        this.y = value;
    }
}
