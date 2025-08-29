package org.game.proto.login;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.proto.ProtoID;

@ProtoID(1005)
public class CSSelectHuman {
    private String humanId;

    public String getHumanId() {
        return humanId;
    }

    public void setHumanId(String humanId) {
        this.humanId = humanId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("humanId", humanId)
                .toString();
    }
}
