package org.game.human;

import org.game.core.GameThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家线程
 */
public class HumanThread extends GameThread {

    private static final Logger logger = LogManager.getLogger(HumanThread.class);

    public static final String NAME = "HumanThread";

    private static final Map<Integer, HumanThread> humanThreads = new ConcurrentHashMap<>();

    private final int index;

    public HumanThread(int index) {
        super(NAME + index);
        this.index = index;
    }

    @Override
    public synchronized void start() {
        super.start();
        humanThreads.put(index, this);
        logger.info("启动玩家线程: {}", index);
    }

    public static HumanThread getHumanThread(int index) {
        return humanThreads.get(index);
    }

    // 获取线程总数量
    public static int getHumanThreadCount() {
        return humanThreads.size();
    }
}