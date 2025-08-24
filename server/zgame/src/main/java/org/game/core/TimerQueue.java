package org.game.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 定时器队列类 (非线程安全)
 * 管理多个定时器，支持一次性延迟执行和周期性执行
 */
public class TimerQueue {

    private static final Logger logger = LogManager.getLogger(TimerQueue.class);

    // 定时器条目类，表示一个定时器
    private static class TimerEntry implements Comparable<TimerEntry> {
        final long id;                    // 定时器ID
        final long delayMs;               // 初始延迟时间（毫秒）
        final long periodMs;              // 周期时间（毫秒），0表示一次性
        final TimerCallback callback;     // 回调函数
        final Param context;              // 上下文参数 (使用具体的 Param 类)
        long nextTriggerTime;             // 下一次触发时间（绝对时间戳）
        boolean cancelled;                // 取消标记

        public TimerEntry(long id, long delayMs, long periodMs, TimerCallback callback, Param context, long now) {
            this.id = id;
            this.delayMs = delayMs;
            this.periodMs = periodMs;
            this.callback = callback;
            this.context = context;
            this.nextTriggerTime = now + delayMs;
            this.cancelled = false;
        }

        @Override
        public int compareTo(TimerEntry other) {
            // 优先队列按下次触发时间排序，最早触发的在前面
            return Long.compare(this.nextTriggerTime, other.nextTriggerTime);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("id", id)
                    .append("delayMs", delayMs)
                    .append("periodMs", periodMs)
                    .append("callback", callback)
                    .append("context", context)
                    .append("nextTriggerTime", nextTriggerTime)
                    .append("cancelled", cancelled)
                    .toString();
        }
    }

    // 回调函数接口
    @FunctionalInterface
    public interface TimerCallback {
        void onTimer(long timerId, Param context); // context 参数类型为 Param
    }

    private final PriorityQueue<TimerEntry> timerQueue;
    private final Map<Long, TimerEntry> activeTimers;
    private long nextTimerId;

    /**
     * 构造函数
     */
    public TimerQueue() {
        // 使用普通PriorityQueue，初始容量11
        this.timerQueue = new PriorityQueue<>();
        this.activeTimers = new HashMap<>();
        this.nextTimerId = 1;
    }

    /**
     * 创建一个定时器
     * @param delayMS 延迟时间（毫秒）
     * @param periodMS 周期时间（毫秒），0表示一次性执行
     * @param callback 回调函数
     * @param context 上下文参数 (使用 Param 类)
     * @return 定时器ID，用于取消定时器
     */
    public long createTimer(long delayMS, long periodMS, TimerCallback callback, Param context) {
        if (delayMS < 0 || periodMS < 0 || callback == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        long now = System.currentTimeMillis();
        long timerId = nextTimerId++;

        TimerEntry entry = new TimerEntry(timerId, delayMS, periodMS, callback, context, now);
        timerQueue.offer(entry);
        activeTimers.put(timerId, entry);

        return timerId;
    }

    public long createTimer(long delayMS, long periodMS, TimerCallback callback) {
        return createTimer(delayMS, periodMS, callback, new Param());
    }

    /**
     * 创建一个一次性延迟执行的定时器
     * @param delayMS 延迟时间（毫秒）
     * @param callback 回调函数
     * @param context 上下文参数 (使用 Param 类)
     * @return 定时器ID，用于取消定时器
     */
    public long delay(long delayMS, TimerCallback callback, Param context) {
        return createTimer(delayMS, 0, callback, context);
    }

    public long delay(long delayMS, TimerCallback callback) {
        return createTimer(delayMS, 0, callback, new Param());
    }



    /**
     * 取消指定的定时器
     * @param timerId 定时器ID
     */
    public void cancelTimer(long timerId) {
        TimerEntry entry = activeTimers.get(timerId);
        if (entry != null) {
            entry.cancelled = true;
            // 注意：这里不从timerQueue中移除，会在update中处理
            activeTimers.remove(timerId);
        }
    }

    /**
     * 更新定时器队列，检查并触发到期的定时器
     * 需要在每帧或定期调用
     * @param now 当前时间戳（毫秒）
     */
    public void update(long now) {
        // 持续检查队列头部的定时器是否到期
        while (!timerQueue.isEmpty()) {
            TimerEntry entry = timerQueue.peek();
            // 如果最近的定时器未到期，跳出循环
            if (entry.nextTriggerTime > now) {
                break;
            }

            // 从队列中移除
            timerQueue.poll();

            // 检查是否已被取消
            if (entry.cancelled) {
                continue; // 已取消，跳过
            }

            // 执行回调
            try {
                entry.callback.onTimer(entry.id, entry.context);
            } catch (Exception e) {
                // 防止回调异常影响其他定时器
                logger.error("执行定时器回调异常, timer={}", entry, e);
            }

            // 如果是周期性定时器，重新安排下一次执行
            if (entry.periodMs > 0) {
                entry.nextTriggerTime = now + entry.periodMs;
                timerQueue.offer(entry);
            }
            // 一次性定时器在执行后自然结束，无需特殊处理
        }
    }

    /**
     * 获取当前活动的定时器数量
     * @return 活动定时器数量
     */
    public int getActiveTimerCount() {
        return activeTimers.size();
    }

    /**
     * 清空所有定时器
     */
    public void clearAll() {
        timerQueue.clear();
        activeTimers.clear();
    }
}