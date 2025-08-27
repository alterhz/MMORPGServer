package org.game.core.human;

import org.game.core.GameThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.human.HumanObject;
import org.game.service.HumanService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 玩家线程
 */
public class HumanThread extends GameThread {

    private static final Logger logger = LogManager.getLogger(HumanThread.class);

    public static final String NAME = "HumanThread";

    private static final Map<Integer, HumanThread> humanThreads = new ConcurrentHashMap<>();
    /**
     * 分配计数
     */
    private static final AtomicLong allocCount = new AtomicLong();

    /**
     * 当前线程索引
     */
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

    public static HumanObject createHumanObject(String id) {
        HumanObject humanObj = new HumanObject(id);
        HumanService humanService = new HumanService(id, humanObj);

        // 随机分配一个线程
        long count = allocCount.getAndIncrement();
        int threadIndex = (int) (count % getHumanThreadCount());

        HumanThread humanThread = getHumanThread(threadIndex);
        humanThread.addGameService(humanService);
        humanService.bindGameThread(humanThread);

        humanThread.runTask(() -> {
            humanService.init();
            humanService.startup();
        });

        return humanObj;
    }

}