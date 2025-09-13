package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1007)
public class CSCreateHuman {

    private String name;

    private String profession;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String value) {
        this.profession = value;
    }
}
