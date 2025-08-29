package org.game.proto.login;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.proto.ProtoID;

@ProtoID(1006)
public class SCSelectHuman {

    private int code;
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
