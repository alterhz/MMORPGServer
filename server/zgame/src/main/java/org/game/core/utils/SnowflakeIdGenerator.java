package org.game.core.utils;

/**
 * Snowflake ID生成器
 * Snowflake ID结构:
 * 1位符号位 + 17位机器ID + 41位时间戳 + 5位序列号
 */
public class SnowflakeIdGenerator {
    
    // 起始时间戳 (2022-01-01)
    private final static long START_TIMESTAMP = 1640995200000L;
    
    // 各部分位数
    private final static long SEQUENCE_BIT = 5;   // 序列号占5位
    private final static long TIMESTAMP_BIT = 41; // 时间戳占41位
    private final static long MACHINE_BIT = 17;   // 机器ID占17位
    
    // 各部分最大值
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_TIMESTAMP = ~(-1L << TIMESTAMP_BIT);
    private final static long MAX_MACHINE = ~(-1L << MACHINE_BIT); // 131071 (0-131071)
    
    // 各部分偏移量 (从低位到高位)
    private final static long SEQUENCE_OFFSET = 0;
    private final static long TIMESTAMP_OFFSET = SEQUENCE_OFFSET + SEQUENCE_BIT;
    private final static long MACHINE_OFFSET = TIMESTAMP_OFFSET + TIMESTAMP_BIT;
    
    private static SnowflakeIdGenerator instance;
    private long machineId;     // 机器ID
    private long sequence = 0;  // 序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳
    
    /**
     * 构造函数
     * @param machineId 机器ID (0-131071)
     */
    private SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE || machineId < 0) {
            throw new IllegalArgumentException("machineId must be between 0 and " + MAX_MACHINE);
        }
        this.machineId = machineId;
    }
    
    /**
     * 初始化ID生成器
     * @param machineId 机器ID (0-131071)
     */
    public static void init(long machineId) {
        instance = new SnowflakeIdGenerator(machineId);
    }
    
    /**
     * 获取ID生成器实例
     * @return ID生成器实例
     */
    public static SnowflakeIdGenerator getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SnowflakeIdGenerator not initialized");
        }
        return instance;
    }
    
    /**
     * 生成下一个ID
     * @return 生成的ID
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        
        // 如果当前时间小于上次生成时间，说明系统时钟回退了，抛出异常
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + 
                    (lastTimestamp - currentTimestamp) + " milliseconds");
        }
        
        // 如果同一毫秒内生成多个ID，则序列号递增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号溢出，需要等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = getNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号重置为0
            sequence = 0;
        }
        
        lastTimestamp = currentTimestamp;
        
        // 检查时间戳是否溢出
        if (currentTimestamp - START_TIMESTAMP > MAX_TIMESTAMP) {
            throw new RuntimeException("Timestamp overflow. Current timestamp: " + currentTimestamp);
        }
        
        // 组装ID (从高位到低位: 符号位 + 机器ID + 时间戳 + 序列号)
        return machineId << MACHINE_OFFSET
                | (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_OFFSET
                | sequence << SEQUENCE_OFFSET;
    }
    
    /**
     * 从ID中解析机器ID
     * @param id 生成的ID
     * @return 机器ID
     */
    public static long parseMachineId(long id) {
        return id >>> MACHINE_OFFSET;
    }
    
    /**
     * 从ID中解析时间戳
     * @param id 生成的ID
     * @return 时间戳
     */
    public static long parseTimestamp(long id) {
        return ((id >>> TIMESTAMP_OFFSET) & MAX_TIMESTAMP) + START_TIMESTAMP;
    }
    
    /**
     * 从ID中解析序列号
     * @param id 生成的ID
     * @return 序列号
     */
    public static long parseSequence(long id) {
        return (id >>> SEQUENCE_OFFSET) & MAX_SEQUENCE;
    }
    
    /**
     * 等待下一毫秒
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 下一毫秒的时间戳
     */
    private long getNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}