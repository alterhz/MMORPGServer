package org.game.proto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

@Proto(ProtoIds.SC_CREATE_HUMAN)
public class SCCreateHuman {
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("humanId")
    private String humanId;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("success")
    private boolean success;

    public SCCreateHuman() {
    }

    public SCCreateHuman(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHumanId() {
        return humanId;
    }

    public void setHumanId(String humanId) {
        this.humanId = humanId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("humanId", humanId)
                .append("message", message)
                .append("success", success)
                .toString();
    }
}