package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1008)
public class SCCreatePlayer {

    private long code;

    private String playerId;

    private String message;

    private boolean success;

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

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean value) {
        this.success = value;
    }
}
