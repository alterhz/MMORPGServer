package org.game.stage.event;

import org.game.core.event.IEvent;
import org.game.stage.unit.Entity;

public class EnterStageEvent implements IEvent {
    private final Entity Entity;

    public EnterStageEvent(Entity Entity) {
        this.Entity = Entity;
    }

    public Entity getUnitObject() {
        return Entity;
    }
}
