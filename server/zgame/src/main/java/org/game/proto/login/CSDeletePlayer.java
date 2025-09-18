package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1009)
public class CSDeletePlayer {

    private String playerId;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String value) {
        this.playerId = value;
    }
}
