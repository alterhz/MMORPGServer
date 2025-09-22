package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1014)
public class SCReconnect {

    private long result;

    private String message;

    public long getResult() {
        return result;
    }

    public void setResult(long value) {
        this.result = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
