package org.game.proto.scene;

import java.util.List;
import org.game.core.message.Proto;

@Proto(value = 50004)
public class UnitDisappearBroadcast {

    private List<Long> unitIds;

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> value) {
        this.unitIds = value;
    }
}
