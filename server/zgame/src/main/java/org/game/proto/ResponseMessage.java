package org.game.proto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 应答消息类，用于服务器对客户端请求的响应
 * 包含响应状态码、消息和数据字段
 */
public class ResponseMessage {
    
    /**
     * 响应状态码
     * 200 - 成功
     * 400 - 请求错误
     * 401 - 未认证
     * 403 - 禁止访问
     * 404 - 资源未找到
     * 500 - 服务器内部错误
     */
    @JsonProperty("code")
    private int code;
    
    /**
     * 响应消息
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * 响应数据
     */
    @JsonProperty("data")
    private String data;
    
    /**
     * 默认构造函数，用于Jackson反序列化
     */
    public ResponseMessage() {
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param code 响应状态码
     * @param message 响应消息
     * @param data 响应数据
     */
    public ResponseMessage(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @return ResponseMessage对象
     */
    public static ResponseMessage success(String data) {
        return new ResponseMessage(0, "Success", data);
    }
    
    /**
     * 创建成功响应（无数据）
     * 
     * @return ResponseMessage对象
     */
    public static ResponseMessage success() {
        return new ResponseMessage(0, "Success", null);
    }
    
    /**
     * 创建错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return ResponseMessage对象
     */
    public static ResponseMessage error(int code, String message) {
        return new ResponseMessage(code, message, null);
    }
    
    /**
     * 获取响应状态码
     * 
     * @return 响应状态码
     */
    public int getCode() {
        return code;
    }
    
    /**
     * 设置响应状态码
     * 
     * @param code 响应状态码
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    /**
     * 获取响应消息
     * 
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 设置响应消息
     * 
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * 获取响应数据
     * 
     * @return 响应数据
     */
    public String getData() {
        return data;
    }
    
    /**
     * 设置响应数据
     * 
     * @param data 响应数据
     */
    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("message", message)
                .append("data", data)
                .toString();
    }
}