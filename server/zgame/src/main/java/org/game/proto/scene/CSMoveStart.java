package org.game.proto.scene;

import org.game.core.message.Proto;

@Proto(value = 50005)
public class CSMoveStart {

    private double x;

    private double y;

    private double z;

    public double getX() {
        return x;
    }

    public void setX(double value) {
        this.x = value;
    }

    public double getY() {
        return y;
    }

    public void setY(double value) {
        this.y = value;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double value) {
        this.z = value;
    }
}
