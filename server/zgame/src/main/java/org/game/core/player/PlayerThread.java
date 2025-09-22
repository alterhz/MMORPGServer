package org.game.core.player;

import org.game.core.GameProcess;
import org.game.core.GameThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanDBManager;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.dao.PlayerDB;
import org.game.global.rpc.IServerService;
import org.game.player.PlayerObject;
import org.game.player.service.PlayerBaseService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 玩家线程
 */
public class PlayerThread extends GameThread {

    private static final Logger logger = LogManager.getLogger(PlayerThread.class);

    public static final String NAME = "PlayerThread";

    private static final Map<Integer, PlayerThread> humanThreads = new ConcurrentHashMap<>();
    /**
     * 分配计数
     */
    private static final AtomicLong allocCount = new AtomicLong();

    /**
     * 当前线程索引
     */
    private final int index;


    public PlayerThread(int index) {
        super(NAME + index);
        this.index = index;
    }

    @Override
    public synchronized void start() {
        super.start();
        humanThreads.put(index, this);
        logger.info("启动玩家线程: {}", index);
    }

    public static PlayerThread getHumanThread(int index) {
        return humanThreads.get(index);
    }

    // 获取线程总数量
    public static int getHumanThreadCount() {
        return humanThreads.size();
    }

    /**
     * 加载玩家对象
     */
    public static void loadPlayerObj(PlayerDB playerDB, ToPoint clientPoint) {
        logger.info("加载玩家对象: {}", playerDB);

        long playerId = playerDB.getPlayerId();

        // 随机分配一个线程
        long count = allocCount.getAndIncrement();
        int threadIndex = (int) (count % getHumanThreadCount());
        PlayerThread playerThread = getHumanThread(threadIndex);

        // PlayerObject连接点
        ToPoint humanPoint = new ToPoint(GameProcess.getName(), playerThread.getName(), String.valueOf(playerId));

        IServerService serverService = ReferenceFactory.getProxy(IServerService.class);
        serverService.updateId();

        PlayerObject humanObj = new PlayerObject(playerDB, humanPoint);
        humanObj.setClientPoint(clientPoint);
        PlayerBaseService humanService = new PlayerBaseService(humanObj);
        playerThread.addGameService(humanService);
        playerThread.runTask(() -> {
            humanService.init();
            humanService.startup();

            HumanDBManager.loadHumanModDB(humanObj);
        });

        PlayerLookup.add(humanObj.getPlayerId(), humanObj);

    }

}