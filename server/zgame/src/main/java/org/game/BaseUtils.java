package org.game;

import org.game.core.utils.SnowflakeIdGenerator;

public class BaseUtils {
    
    // 服务器id与雪花算法的machineId转换
    public static final long MAX_MACHINE_ID = 81920; // 机器ID最大值
    
    /**
     * 根据服务器ID计算机器ID
     * @param serverId 服务器ID
     * @return 机器ID
     */
    public static long getMachineId(long serverId) {
        // 服务器ID从20001开始，需要减去基准值再计算
        long actualServerId = serverId - 20001;
        return actualServerId % MAX_MACHINE_ID;
    }
    
    /**
     * 根据机器ID计算服务器ID
     * @param machineId 机器ID
     * @return 服务器ID
     */
    public static long getServerId(long machineId) {
        return machineId + 20001;
    }
    
    /**
     * 初始化雪花算法ID生成器
     * @param serverId 服务器ID
     */
    public static void init(long serverId) {
        long machineId = getMachineId(serverId);
        
        // 确保机器ID在有效范围内
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("机器ID超出范围: " + machineId);
        }
        
        SnowflakeIdGenerator.init(machineId);
    }
    
    /**
     * 生成下一个ID
     * @return 生成的ID
     */
    public static long nextId() {
        return SnowflakeIdGenerator.getInstance().nextId();
    }
}