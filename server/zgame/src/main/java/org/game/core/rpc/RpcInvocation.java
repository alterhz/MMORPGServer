package org.game.core.rpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.utils.JsonUtils;

import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unused")
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

    /**
     * 根据parameterTypes中存储的类型信息，将parameters中的参数还原为原始数据类型
     * @return 还原类型后的参数列表
     */
    @SuppressWarnings("unused")
    public List<Object> getTypedParameters() {
        if (parameters == null || parameterTypes == null || parameters.size() != parameterTypes.size()) {
            return parameters;
        }

        List<Object> typedParameters = new ArrayList<>(parameters.size());
        for (int i = 0; i < parameters.size(); i++) {
            Object param = parameters.get(i);
            String typeName = parameterTypes.get(i);
            
            Object typedParam = convertToType(param, typeName);
            typedParameters.add(typedParam);
        }
        
        return typedParameters;
    }

    /**
     * 根据类型名称将参数转换为相应的类型
     * @param param 参数值
     * @param typeName 类型名称
     * @return 转换后的参数
     */
    @SuppressWarnings("unchecked")
    private Object convertToType(Object param, String typeName) {
        if (param == null) {
            return null;
        }

        // 基本数据类型及其包装类处理
        switch (typeName) {
            case "int":
            case "java.lang.Integer":
                if (param instanceof Integer) {
                    return param;
                } else if (param instanceof Number) {
                    return ((Number) param).intValue();
                } else {
                    return Integer.parseInt(param.toString());
                }
                
            case "long":
            case "java.lang.Long":
                if (param instanceof Long) {
                    return param;
                } else if (param instanceof Number) {
                    return ((Number) param).longValue();
                } else {
                    return Long.parseLong(param.toString());
                }
                
            case "double":
            case "java.lang.Double":
                if (param instanceof Double) {
                    return param;
                } else if (param instanceof Number) {
                    return ((Number) param).doubleValue();
                } else {
                    return Double.parseDouble(param.toString());
                }
                
            case "float":
            case "java.lang.Float":
                if (param instanceof Float) {
                    return param;
                } else if (param instanceof Number) {
                    return ((Number) param).floatValue();
                } else {
                    return Float.parseFloat(param.toString());
                }
                
            case "boolean":
            case "java.lang.Boolean":
                if (param instanceof Boolean) {
                    return param;
                } else {
                    return Boolean.parseBoolean(param.toString());
                }
                
            case "byte":
            case "java.lang.Byte":
                if (param instanceof Byte) {
                    return param;
                } else if (param instanceof Number) {
                    return ((Number) param).byteValue();
                } else {
                    return Byte.parseByte(param.toString());
                }
                
            case "short":
            case "java.lang.Short":
                if (param instanceof Short) {
                    return param;
                } else if (param instanceof Number) {
                    return ((Number) param).shortValue();
                } else {
                    return Short.parseShort(param.toString());
                }
                
            case "char":
            case "java.lang.Character":
                if (param instanceof Character) {
                    return param;
                } else if (param instanceof String && ((String) param).length() == 1) {
                    return ((String) param).charAt(0);
                }
                break;
                
            case "java.lang.String":
                return param.toString();
                
            default:
                // 对于其他复杂类型，尝试通过JSON反序列化还原对象
                try {
                    Class<?> clazz = Class.forName(typeName);
                    if (param instanceof String) {
                        // 如果参数已经是字符串形式的JSON，直接反序列化
                        return JsonUtils.tryDecode((String) param, (Class<Object>) clazz);
                    } else {
                        // 如果参数是Map等形式，先序列化为JSON字符串再反序列化为目标类型
                        String json = JsonUtils.tryEncode(param);
                        return JsonUtils.tryDecode(json, (Class<Object>) clazz);
                    }
                } catch (Exception e) {
                    // 类型转换失败时，保持原始参数
                    return param;
                }
        }
        
        return param;
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