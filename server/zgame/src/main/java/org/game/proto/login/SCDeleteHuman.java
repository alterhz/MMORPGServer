package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1010)
public class SCDeleteHuman {

    private long code;

    private String humanId;

    private String message;

    public long getCode() {
        return code;
    }

    public void setCode(long value) {
        this.code = value;
    }

    public String getHumanId() {
        return humanId;
    }

    public void setHumanId(String value) {
        this.humanId = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
