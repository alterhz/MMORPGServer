package org.game.proto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

@Proto(ProtoIds.CS_DELETE_HUMAN)
public class CSDeleteHuman {
    
    private String humanId;
    
    public CSDeleteHuman() {
    }
    
    public CSDeleteHuman(String humanId) {
        this.humanId = humanId;
    }
    
    public String getHumanId() {
        return humanId;
    }
    
    public void setHumanId(String humanId) {
        this.humanId = humanId;
    }
    
    @Override
    public String toString() {
        return "CSDeleteHuman{" +
                "humanId='" + humanId + '\'' +
                '}';
    }
}