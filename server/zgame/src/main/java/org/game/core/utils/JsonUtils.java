package org.game.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JSON工具类，提供泛型的编码和解码功能
 * 使用Jackson ObjectMapper进行JSON序列化和反序列化操作
 */
public class JsonUtils {
    private static final Logger logger = LogManager.getLogger(JsonUtils.class);
    
    /**
     * 线程安全的ObjectMapper实例
     * ObjectMapper是线程安全的，只要配置不变，就可以共享使用
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将对象序列化为JSON字符串
     * 
     * @param obj 要序列化的对象
     * @param <T> 对象类型
     * @return 序列化后的JSON字符串
     * @throws JsonProcessingException 当序列化失败时抛出异常
     */
    public static <T> String encode(T obj) throws JsonProcessingException {
        if (obj == null) {
            return null;
        }
        return objectMapper.writeValueAsString(obj);
    }
    
    /**
     * 将JSON字符串反序列化为指定类型的对象
     * 
     * @param json JSON字符串
     * @param clazz 目标类型Class对象
     * @param <T> 目标类型
     * @return 反序列化后的对象
     * @throws JsonProcessingException 当反序列化失败时抛出异常
     */
    public static <T> T decode(String json, Class<T> clazz) throws JsonProcessingException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return objectMapper.readValue(json, clazz);
    }
    
    /**
     * 尝试将对象序列化为JSON字符串，如果失败则返回null
     * 
     * @param obj 要序列化的对象
     * @param <T> 对象类型
     * @return 序列化后的JSON字符串，失败时返回null
     */
    public static <T> String tryEncode(T obj) {
        try {
            return encode(obj);
        } catch (Exception e) {
            logger.error("Failed to encode object to JSON. obj={}", obj, e);
            return null;
        }
    }
    
    /**
     * 尝试将JSON字符串反序列化为指定类型的对象，如果失败则返回null
     * 
     * @param json JSON字符串
     * @param clazz 目标类型Class对象
     * @param <T> 目标类型
     * @return 反序列化后的对象，失败时返回null
     */
    public static <T> T tryDecode(String json, Class<T> clazz) {
        try {
            return decode(json, clazz);
        } catch (Exception e) {
            logger.error("Failed to decode JSON to object. json={}", json, e);
            return null;
        }
    }
}