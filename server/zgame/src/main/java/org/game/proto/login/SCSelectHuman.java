package org.game.proto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

@Proto(ProtoIds.SC_SELECT_HUMAN)
public class SCSelectHuman {

    @JsonProperty("code")
    private int code;
    
    @JsonProperty("message")
    private String message;

    public SCSelectHuman()
    {

    }

    public SCSelectHuman(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("message", message)
                .toString();
    }
}