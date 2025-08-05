package org.game.core.rpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class RpcInvocation {
    private FromPoint fromPoint;
    private ToPoint toPoint;
    private String methodName;
    private List<Object> parameters;
    private List<String> parameterTypes;

    public RpcInvocation() {}

    @JsonCreator
    public RpcInvocation(@JsonProperty("fromPoint") FromPoint fromPoint,
                         @JsonProperty("toPoint") ToPoint toPoint,
                         @JsonProperty("methodName") String methodName,
                         @JsonProperty("parameters") List<Object> parameters,
                         @JsonProperty("parameterTypes") List<String> parameterTypes) {
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.methodName = methodName;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
    }

    public FromPoint getFromPoint() {
        return fromPoint;
    }

    public void setFromPoint(FromPoint fromPoint) {
        this.fromPoint = fromPoint;
    }

    public ToPoint getToPoint() {
        return toPoint;
    }

    public void setToPoint(ToPoint toPoint) {
        this.toPoint = toPoint;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fromPoint", fromPoint)
                .append("toPoint", toPoint)
                .append("methodName", methodName)
                .append("parameters", parameters)
                .append("parameterTypes", parameterTypes)
                .toString();
    }
}