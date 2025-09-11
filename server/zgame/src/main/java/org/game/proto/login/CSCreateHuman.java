package org.game.proto.login;

import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

@Proto(ProtoIds.CS_CREATE_HUMAN)
public class CSCreateHuman {
    
    private String name;
    
    private String profession;
    
    public CSCreateHuman() {
    }
    
    public CSCreateHuman(String name, String profession) {
        this.name = name;
        this.profession = profession;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProfession() {
        return profession;
    }
    
    public void setProfession(String profession) {
        this.profession = profession;
    }
    
    @Override
    public String toString() {
        return "CSCreateHuman{" +
                "name='" + name + '\'' +
                ", profession='" + profession + '\'' +
                '}';
    }
}