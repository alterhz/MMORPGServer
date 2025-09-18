package org.game.player;

public class PlayerModBase {

    protected final PlayerObject playerObj;

    public PlayerModBase(PlayerObject playerObj) {
        this.playerObj = playerObj;
    }

    protected PlayerObject getPlayerObj() {
        return playerObj;
    }


    protected void onPulse(long now) {

    }

    protected void onPulseSec(long now) {

    }

    public <T extends PlayerModBase> T getMod(Class<T> clazz) {
        return playerObj.getMod(clazz);
    }

}