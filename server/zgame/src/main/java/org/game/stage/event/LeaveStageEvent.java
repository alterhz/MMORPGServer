package org.game.stage.event;

import org.game.core.event.IEvent;
import org.game.stage.entity.Entity;

public class LeaveStageEvent implements IEvent {

    private final Entity Entity;

    public LeaveStageEvent(Entity Entity) {
        this.Entity = Entity;
    }
    public Entity getUnitObject() {
        return Entity;
    }
}
