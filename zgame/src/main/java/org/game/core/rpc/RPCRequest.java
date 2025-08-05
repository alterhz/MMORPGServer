package org.game.core.rpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RPCRequest {
    private String requestId;
    private RpcInvocation invocation;

    public RPCRequest() {}

    @JsonCreator
    public RPCRequest(@JsonProperty("requestId") String requestId,
                      @JsonProperty("invocation") RpcInvocation invocation) {
        this.requestId = requestId;
        this.invocation = invocation;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public RpcInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(RpcInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("requestId", requestId)
                .append("invocation", invocation)
                .toString();
    }
}