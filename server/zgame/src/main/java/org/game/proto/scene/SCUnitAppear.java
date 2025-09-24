package org.game.proto.scene;

import java.util.List;
import org.game.core.message.Proto;

@Proto(value = 50003)
public class SCUnitAppear {

    private List<Unit> units;

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> value) {
        this.units = value;
    }
}
