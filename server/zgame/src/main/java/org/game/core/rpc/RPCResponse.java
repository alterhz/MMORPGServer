package org.game.core.rpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RPCResponse {
    private String requestId;
    private FromPoint fromPoint;
    private Object data;
    private String error;


    public RPCResponse() {}

    @JsonCreator
    public RPCResponse(@JsonProperty("requestId") String requestId,
                       @JsonProperty("fromPoint") FromPoint fromPoint,
                       @JsonProperty("data") Object data,
                       @JsonProperty("error") String error) {
        this.requestId = requestId;
        this.fromPoint = fromPoint;
        this.data = data;
        this.error = error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setFromPoint(FromPoint fromPoint) {
        this.fromPoint = fromPoint;
    }

    public FromPoint getFromPoint() {
        return fromPoint;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("requestId", requestId)
                .append("fromPoint", fromPoint)
                .append("data", data)
                .append("error", error)
                .toString();
    }
}