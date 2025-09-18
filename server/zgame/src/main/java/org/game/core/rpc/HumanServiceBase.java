package org.game.core.rpc;

import org.game.player.PlayerObject;

public class HumanServiceBase {

    protected final PlayerObject humanObj;

    public HumanServiceBase(PlayerObject humanObj) {
        this.humanObj = humanObj;
    }

    public PlayerObject getHumanObj() {
        return humanObj;
    }
}
