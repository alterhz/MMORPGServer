package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1013)
public class CSReconnect {

    private long playerId;

    private String token;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long value) {
        this.playerId = value;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }
}
