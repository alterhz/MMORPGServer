package org.game.core;

/**
 * 定时器类，用于按固定间隔执行任务
 */
public class TickTimer {
    // 间隔时间（秒）
    private final int intervalSeconds;
    // 上次执行时间戳
    private long lastExecuteTime;
    
    /**
     * 构造函数
     * @param intervalMS 执行间隔（毫秒）
     */
    public TickTimer(int intervalMS) {
        this.intervalSeconds = intervalMS;
        this.lastExecuteTime = System.currentTimeMillis();
    }
    
    /**
     * 更新定时器状态
     * @param currentTime 当前时间戳
     * @return 如果到达执行时间返回true，否则返回false
     */
    public boolean update(long currentTime) {
        if (currentTime - lastExecuteTime >= intervalSeconds) {
            lastExecuteTime = currentTime;
            return true;
        }
        return false;
    }
    
    /**
     * 获取间隔时间（秒）
     * @return 间隔时间
     */
    public int getIntervalSeconds() {
        return intervalSeconds;
    }
    
    /**
     * 重置上次执行时间
     */
    public void reset() {
        this.lastExecuteTime = System.currentTimeMillis();
    }
}