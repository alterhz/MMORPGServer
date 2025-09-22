package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1012)
public class SCSendToClientEnd {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }
}
