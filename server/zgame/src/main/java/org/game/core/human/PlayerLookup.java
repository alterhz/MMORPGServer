package org.game.core.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameThread;
import org.game.player.PlayerObject;
import org.game.player.service.PlayerService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人物查找
 */
public class PlayerLookup {

    public static final Logger logger = LogManager.getLogger(PlayerLookup.class);

    private static final Map<Long, PlayerLocation> playerIdMap = new ConcurrentHashMap<>();
    private static final Map<String, PlayerLocation> accountMap = new ConcurrentHashMap<>();

    public static void add(long humanId, String account, String threadName) {
        // 添加ID映射
        playerIdMap.put(humanId, new PlayerLocation(humanId, threadName, account));
        // 添加账号映射
        accountMap.put(account, new PlayerLocation(humanId, threadName, account));
    }

    public static void remove(String humanId) {
        PlayerLocation playerLocation = playerIdMap.get(humanId);
        if (playerLocation != null) {
            playerIdMap.remove(humanId);
            accountMap.remove(playerLocation.getAccount());
        }
    }

    /**
     * 获取玩家线程名称
     */
    public static String getHumanThreadName(String humanId) {
        PlayerLocation playerLocation = playerIdMap.get(humanId);
        if (playerLocation != null) {
            return playerLocation.getThreadName();
        }
        return null;
    }

    public static PlayerLocation getByHumanIdSafely(String humanId) {
        return playerIdMap.get(humanId);
    }

    public static PlayerLocation getByAccountSafely(String account) {
        return accountMap.get(account);
    }

    public static void KickPlayerByAccount(String account) {
        PlayerLocation playerLocation = accountMap.get(account);
        if (playerLocation != null) {
            GameThread humanThread = GameProcess.getGameThread(playerLocation.threadName);
            if (humanThread != null) {
                humanThread.runTask(() -> {
                    PlayerService humanService = (PlayerService)humanThread.getGameService(String.valueOf(playerLocation.playerId));
                    if (humanService != null) {
                        PlayerObject humanObj = humanService.getHumanObj();
                        humanObj.disconnect();
                        humanThread.removeGameService(humanService);
                        logger.info("KickPlayerByAccount: {}", humanObj);
                    }
                });
            }
        }
    }

    public static class PlayerLocation {
        private final long playerId;
        private final String threadName;
        private final String account;
        
        public PlayerLocation(long playerId, String threadName, String account) {
            this.playerId = playerId;
            this.threadName = threadName;
            this.account = account;
        }

        public long getPlayerId() {
            return playerId;
        }

        public String getThreadName() {
            return threadName;
        }

        public String getAccount() {
            return account;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("humanId", playerId)
                    .append("threadName", threadName)
                    .append("account", account)
                    .toString();
        }
    }

}
