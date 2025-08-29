package org.game.proto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.message.Proto;
import org.game.proto.ProtoIds;
import org.game.proto.common.HumanInfo;

import java.util.List;

@Proto(ProtoIds.SC_QUERY_HUMANS)
public class SCQueryHumans {
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("humanList")
    private List<HumanInfo> humanList;
    
    @JsonProperty("message")
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