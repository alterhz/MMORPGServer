package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1006)
public class SCSelectHuman {

    private long code;

    private String message;

    public long getCode() {
        return code;
    }

    public void setCode(long value) {
        this.code = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
