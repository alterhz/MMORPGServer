package org.game.proto.login;

import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

@Proto(ProtoIds.SC_TEST)
public class SCTest {

    private String message;

    public SCTest() {
    }

    public SCTest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
