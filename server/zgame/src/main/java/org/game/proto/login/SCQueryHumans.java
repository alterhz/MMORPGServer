package org.game.proto.login;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.proto.ProtoID;
import org.game.proto.common.HumanInfo;

import java.util.List;

@ProtoID(1004)
public class SCQueryHumans {
    private int code;
    private List<HumanInfo> humanList;
    private String message;

    public SCQueryHumans()
    {
    }

    public SCQueryHumans(int code, String message)
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

    public List<HumanInfo> getHumanList() {
        return humanList;
    }

    public void setHumanList(List<HumanInfo> humanList) {
        this.humanList = humanList;
    }
}