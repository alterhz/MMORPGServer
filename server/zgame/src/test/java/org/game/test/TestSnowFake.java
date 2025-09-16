package org.game.test;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法简单示例
 * 
 * hutool的Snowflake实现中，数据中心ID和机器ID的取值范围都是0-31
 * 如果需要支持更多机器，可以采用以下策略：
 * 1. 多个数据中心，每个数据中心最多32台机器
 * 2. 使用自定义Snowflake实现（如项目中的SnowflakeIdGenerator），支持0-1023的机器ID范围
 */
public class TestSnowFake {
    
    public static void main(String[] args) {
        // 使用hutool的雪花算法生成ID
        // 创建Snowflake对象，参数为数据中心ID(0-31)和机器ID(0-31)
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        
        System.out.println("使用hutool的雪花算法生成ID示例:");
        
        // 生成一些ID示例
        for (int i = 0; i < 10; i++) {
            long id = snowflake.nextId();
            System.out.println("生成的ID: " + id);
        }
        
        // 生成带格式的ID（字符串形式）
        System.out.println("\n字符串形式的ID:");
        for (int i = 0; i < 5; i++) {
            String idStr = snowflake.nextIdStr();
            System.out.println("生成的ID字符串: " + idStr);
        }
        
        // 获取Snowflake的各部分信息
        System.out.println("\n解析ID结构:");
        long id = snowflake.nextId();
        System.out.println("生成的ID: " + id);
        System.out.println("ID的二进制表示: " + Long.toBinaryString(id));
        // getDataCenterId和getWorkerId方法需要传入生成的ID来解析其中的信息
        System.out.println("数据中心ID: " + snowflake.getDataCenterId(id));
        System.out.println("机器ID: " + snowflake.getWorkerId(id));
        System.out.println("生成时间: " + snowflake.getGenerateDateTime(id));
        
        // 500台机器的ID分配策略示例
        demonstrate500MachinesStrategy();
    }
    
    /**
     * 演示500台机器的ID分配策略
     */
    private static void demonstrate500MachinesStrategy() {
        System.out.println("\n=== 500台机器的ID分配策略 ===");
        
        // 策略1: 使用多个数据中心 (推荐)
        System.out.println("策略1: 使用多个数据中心");
        System.out.println("可以使用16个数据中心(0-15)，每个数据中心32台机器(0-31)");
        System.out.println("总共支持 16 * 32 = 512 台机器，满足500台机器的需求");
        
        // 示例：为前10台机器分配ID
        for (int machineIndex = 1; machineIndex <= 10; machineIndex++) {
            // 计算数据中心ID和机器ID
            long dataCenterId = (machineIndex - 1) / 32;  // 数据中心ID
            long workerId = (machineIndex - 1) % 32;      // 机器ID
            
            System.out.printf("机器编号: %d, 数据中心ID: %d, 机器ID: %d%n", 
                            machineIndex, dataCenterId, workerId);
        }
        
        // 策略2: 使用项目自定义的SnowflakeIdGenerator
        System.out.println("\n策略2: 使用项目自定义的SnowflakeIdGenerator");
        System.out.println("项目中自定义的SnowflakeIdGenerator支持0-1023的机器ID范围");
        System.out.println("可以直接使用1-500作为机器ID");
        
        // 示例：使用项目自定义的SnowflakeIdGenerator
        System.out.println("示例代码:");
        System.out.println("// SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(machineId);");
        System.out.println("// long id = idGenerator.nextId();");
    }
}