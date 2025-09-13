package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1102)
public class SCTest {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }
}
