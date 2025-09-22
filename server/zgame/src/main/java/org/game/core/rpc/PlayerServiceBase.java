package org.game.core.rpc;

import org.game.player.PlayerObject;

public class PlayerServiceBase {

    protected final PlayerObject playerObj;

    public PlayerServiceBase(PlayerObject playerObj) {
        this.playerObj = playerObj;
    }

    public PlayerObject getPlayerObj() {
        return playerObj;
    }
}
