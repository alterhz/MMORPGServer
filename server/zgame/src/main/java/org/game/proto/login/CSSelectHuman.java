package org.game.proto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

@Proto(ProtoIds.CS_SELECT_HUMAN)
public class CSSelectHuman {
    @JsonProperty("humanId")
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