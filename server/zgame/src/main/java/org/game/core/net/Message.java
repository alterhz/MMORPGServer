package org.game.core.net;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.utils.JsonUtils;

public class Message {
    @JsonProperty("protoID")
    private final int protoID;
    
    @JsonProperty("jsonStr")
    private final String jsonStr;

    @JsonCreator
    public Message(@JsonProperty("protoID") int protoID, @JsonProperty("jsonStr") String jsonStr) {
        this.protoID = protoID;
        this.jsonStr = jsonStr;
    }

    public int getProtoID() {
        return protoID;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public static Message createMessage(int protoID, String jsonStr) {
        return new Message(protoID, jsonStr);
    }

    public static <T> Message createMessage(int protoID, T obj) {
        String jsonStr = JsonUtils.tryEncode(obj);
        return new Message(protoID, jsonStr);
    }

    public <T> T getProto(Class<T> clazz) {
        return JsonUtils.tryDecode(jsonStr, clazz);
    }

    public static Message fromBytes(byte[] bytes) {
        // 将byte数组转换为msgid和content
        // 格式: msgid(4字节) + content内容
        int protoID = (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        String jsonStr = new String(bytes, 4, bytes.length - 4);

        return new Message(protoID, jsonStr);
    }

    public byte[] toBytes() {
        // 将msgid和content转换为byte数组
        // 格式: msgid(4字节) + content内容
        byte[] contentBytes = jsonStr.getBytes();
        byte[] result = new byte[4 + contentBytes.length];
        
        // 写入msgid (4字节)
        result[0] = (byte) (protoID >> 24);
        result[1] = (byte) (protoID >> 16);
        result[2] = (byte) (protoID >> 8);
        result[3] = (byte) protoID;
        
        // 写入content内容
        System.arraycopy(contentBytes, 0, result, 4, contentBytes.length);
        
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("protoID", protoID)
                .append("jsonStr", jsonStr)
                .toString();
    }
}