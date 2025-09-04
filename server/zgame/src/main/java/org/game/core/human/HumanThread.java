package org.game.core.human;

import org.game.core.GameThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanDBManager;
import org.game.core.rpc.ToPoint;
import org.game.dao.HumanDB;
import org.game.human.HumanObject;
import org.game.human.service.HumanService;

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

    /**
     * 加载玩家对象
     */
    public static void loadHumanObject(HumanDB humanDB, ToPoint clientPoint) {
        logger.info("加载玩家对象: {}", humanDB);

        HumanObject humanObj = new HumanObject(humanDB.getId().toHexString());
        humanObj.setHumanDB(humanDB);
        humanObj.setClientPoint(clientPoint);
        HumanService humanService = new HumanService(humanObj);

        // 随机分配一个线程
        long count = allocCount.getAndIncrement();
        int threadIndex = (int) (count % getHumanThreadCount());

        HumanThread humanThread = getHumanThread(threadIndex);
        humanThread.addGameService(humanService);
        humanService.bindGameThread(humanThread);

        HumanLookup.add(humanObj.getId(), humanObj.getAccount(), humanThread.getName());

        humanThread.runTask(() -> {
            humanService.init();
            humanService.startup();

            HumanDBManager.loadHumanModDB(humanObj);
        });

    }

}