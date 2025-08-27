package org.game.core.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameThread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接线程类，负责处理网络连接相关的任务
 * 继承自GameThread，具备游戏线程的基本功能
 */
public class ConnThread extends GameThread {

    private static final Logger logger = LogManager.getLogger(ConnThread.class);

    public static final String NAME = "ConnThread";

    private static final Map<Integer, ConnThread> connThreads = new ConcurrentHashMap<>();

    private final int index;
    /**
     * 构造方法
     * @param index 线程索引
     */
    public ConnThread(int index) {
        super(NAME + index);
        this.index = index;
    }

    @Override
    public synchronized void start() {
        super.start();
        connThreads.put(index, this);
        logger.info("启动连接线程: {}", index);
    }

    public static ConnThread getConnThread(int index) {
        return connThreads.get(index);
    }

    // 获取线程总数量
    public static int getConnThreadCount() {
        return connThreads.size();
    }
}
