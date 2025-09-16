package org.game.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个通用的参数容器类，使用 Map<String, Object> 存储任意数据。
 * 支持通过泛型安全地获取特定类型的值。
 */
public class Param {
    private final Map<String, Object> data;

    /**
     * 构造一个新的 Param 实例。
     */
    public Param() {
        this.data = new HashMap<>();
    }



    /**
     * 构造一个新的 Param 实例，并使用提供的 Map 初始化数据。
     *
     * @param value 用于初始化的键值对映射，不能为空
     * @throws IllegalArgumentException 如果 value 为 null
     */
    public Param(Map<String, Object> value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.data = new HashMap<>(value);
    }


    /**
     * 向容器中添加或更新一个键值对。
     *
     * @param key   键，不能为空
     * @param value 值，可以是任意 Object 类型
     * @return 当前 Param 实例，支持链式调用
     * @throws IllegalArgumentException 如果 key 为 null
     */
    public Param put(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        data.put(key, value);
        return this;
    }

    /**
     * 从容器中获取指定键对应的值，并尝试转换为指定的泛型类型。
     *
     * @param key          键
     * @param defaultValue 如果键不存在或类型转换失败时返回的默认值
     * @param <T>          期望返回的类型
     * @return 指定类型的值，如果无法获取或转换则返回默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            // 对于基本类型包装类，尝试进行类型转换
            if (defaultValue instanceof Integer) {
                if (value instanceof Number) {
                    return (T) Integer.valueOf(((Number) value).intValue());
                } else if (value instanceof String) {
                    return (T) Integer.valueOf(Integer.parseInt((String) value));
                }
            } else if (defaultValue instanceof Long) {
                if (value instanceof Number) {
                    return (T) Long.valueOf(((Number) value).longValue());
                } else if (value instanceof String) {
                    return (T) Long.valueOf(Long.parseLong((String) value));
                }
            } else if (defaultValue instanceof Boolean) {
                if (value instanceof Boolean) {
                    return (T) value;
                } else if (value instanceof String) {
                    return (T) Boolean.valueOf(Boolean.parseBoolean((String) value));
                }
            } else if (defaultValue instanceof String) {
                // 对于 String，调用 toString() 以处理非 String 对象
                return (T) value.toString();
            }
            // 尝试直接强制转换（适用于其他引用类型）
            return (T) value;
        } catch (Exception e) {
            // 类型转换失败（如字符串无法解析为数字），返回默认值
            return defaultValue;
        }
    }

    /**
     * 从容器中获取指定键对应的值，并尝试转换为指定的泛型类型。
     * 如果键不存在或类型转换失败，返回 null。
     *
     * @param key 键
     * @param <T> 期望返回的类型
     * @return 指定类型的值，如果无法获取或转换则返回 null
     */
    public <T> T get(String key) {
        return get(key, null);
    }

    /**
     * 检查容器中是否包含指定的键。
     *
     * @param key 要检查的键
     * @return 如果包含该键则返回 true，否则返回 false
     */
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    /**
     * 获取容器中键值对的数量。
     *
     * @return 键值对的数量
     */
    public int size() {
        return data.size();
    }

    /**
     * 清空容器中的所有数据。
     */
    public void clear() {
        data.clear();
    }

    /**
     * 获取底层 Map 的不可变视图（可选，用于调试或只读访问）。
     *
     * @return 底层 Map 的不可变副本
     */
    public Map<String, Object> toMap() {
        return new HashMap<>(data);
    }

    @Override
    public String toString() {
        return "Param{" +
                "data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Param param = (Param) o;
        return data.equals(param.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    // --- 为常用类型提供便捷的 get 方法（可选，增强易用性） ---
    /**
     * 便捷方法：获取 Integer 值。
     */
    public Integer getInt(String key) {
        return get(key, (Integer) null);
    }

    /**
     * 便捷方法：获取 Integer 值，提供默认值。
     */
    public Integer getInt(String key, Integer defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * 便捷方法：获取 Long 值。
     */
    public Long getLong(String key) {
        return get(key, (Long) null);
    }

    /**
     * 便捷方法：获取 Long 值，提供默认值。
     */
    public Long getLong(String key, Long defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * 便捷方法：获取 String 值。
     */
    public String getString(String key) {
        return get(key, (String) null);
    }

    /**
     * 便捷方法：获取 String 值，提供默认值。
     */
    public String getString(String key, String defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * 便捷方法：获取 Boolean 值。
     */
    public Boolean getBoolean(String key) {
        return get(key, (Boolean) null);
    }

    /**
     * 便捷方法：获取 Boolean 值，提供默认值。
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return get(key, defaultValue);
    }
}
