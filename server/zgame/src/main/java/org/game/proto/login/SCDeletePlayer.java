package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1010)
public class SCDeletePlayer {

    private long code;

    private String playerId;

    private String message;

    public long getCode() {
        return code;
    }

    public void setCode(long value) {
        this.code = value;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String value) {
        this.playerId = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
