package org.game.test;

import org.game.core.utils.SnowflakeIdGenerator;

/**
 * Snowflake ID生成器测试类
 */
public class SnowflakeIdGeneratorTest {
    
    public static void main(String[] args) {
        // 测试机器ID为20001的情况
        long machineId = 20001;
        SnowflakeIdGenerator.init(machineId);
        SnowflakeIdGenerator idGenerator = SnowflakeIdGenerator.getInstance();
        
        System.out.println("Snowflake ID生成器测试:");
        System.out.println("机器ID: " + machineId);
        System.out.println("=====================================");
        
        // 生成10个ID示例
        System.out.println("生成ID示例:");
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.nextId();
            long parsedMachineId = SnowflakeIdGenerator.parseMachineId(id);
            long parsedTimestamp = SnowflakeIdGenerator.parseTimestamp(id);
            long parsedSequence = SnowflakeIdGenerator.parseSequence(id);
            
            System.out.println("ID " + (i+1) + ": " + id);
            System.out.println("  机器ID: " + parsedMachineId + " (正确: " + (parsedMachineId == machineId) + ")");
            System.out.println("  时间戳: " + parsedTimestamp + " (" + new java.util.Date(parsedTimestamp) + ")");
            System.out.println("  序列号: " + parsedSequence);
            System.out.println();
        }
        
        // 验证ID以机器ID开头
        System.out.println("验证ID以机器ID开头:");
        long testId = idGenerator.nextId();
        String idStr = String.valueOf(testId);
        String machineIdStr = String.valueOf(machineId);
        
        System.out.println("生成的ID: " + testId);
        System.out.println("ID字符串: " + idStr);
        System.out.println("机器ID字符串: " + machineIdStr);
        System.out.println("ID是否以机器ID开头: " + idStr.startsWith(machineIdStr));
        System.out.println();
        
        // 测试同一毫秒内生成多个ID（序列号递增）
        System.out.println("测试同一毫秒内生成多个ID:");
        try {
            long firstId = idGenerator.nextId();
            Thread.sleep(1); // 确保下一毫秒
            long secondId = idGenerator.nextId();
            
            long firstSequence = SnowflakeIdGenerator.parseSequence(firstId);
            long secondSequence = SnowflakeIdGenerator.parseSequence(secondId);
            
            System.out.println("第一个ID: " + firstId + ", 序列号: " + firstSequence);
            System.out.println("第二个ID: " + secondId + ", 序列号: " + secondSequence);
            System.out.println("序列号重置为0: " + (secondSequence == 0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();

        // 预热
        System.out.println("预热:");
        for (int i = 0; i < 100000; i++) {
            idGenerator.nextId();
        }

        // 性能测试
        System.out.println("性能测试:");
        long start = System.currentTimeMillis();
        int count = 100000;
        for (int i = 0; i < count; i++) {
            idGenerator.nextId();
        }
        long end = System.currentTimeMillis();
        System.out.println("生成" + count + "个ID耗时: " + (end - start) + "ms");
        System.out.println("平均每秒生成ID数: " + (count * 1000.0 / (end - start)));
    }
}