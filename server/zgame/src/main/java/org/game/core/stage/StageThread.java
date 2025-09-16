package org.game.core.stage;

import org.game.core.GameThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 场景线程
 */
public class StageThread extends GameThread {

    private static final Logger logger = LogManager.getLogger(StageThread.class);

    public static final String NAME = "StageThread";

    private static final Map<Integer, StageThread> stageThreads = new ConcurrentHashMap<>();
    
    /**
     * 分配计数
     */
    private static final AtomicLong allocCount = new AtomicLong();

    /**
     * 当前线程索引
     */
    private final int index;


    public StageThread(int index) {
        super(NAME + index);
        this.index = index;
    }

    @Override
    public synchronized void start() {
        super.start();
        stageThreads.put(index, this);
        logger.info("启动场景线程: {}", index);
    }

    public static StageThread getStageThread(int index) {
        if (index < 0 || index >= stageThreads.size()) {
            throw new IllegalArgumentException("Index " + index + " is out of range. index must be between 0 and " + (stageThreads.size() - 1));
        }
        return stageThreads.get(index);
    }

    public static String getStageThreadName(int index) {
        if (index < 0 || index >= stageThreads.size()) {
            throw new IllegalArgumentException("Index " + index + " is out of range. index must be between 0 and " + (stageThreads.size() - 1));
        }
        return NAME + index;
    }

    // 获取线程总数量
    public static int getStageThreadCount() {
        return stageThreads.size();
    }
}