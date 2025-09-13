package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1009)
public class CSDeleteHuman {

    private String humanId;

    public String getHumanId() {
        return humanId;
    }

    public void setHumanId(String value) {
        this.humanId = value;
    }
}
