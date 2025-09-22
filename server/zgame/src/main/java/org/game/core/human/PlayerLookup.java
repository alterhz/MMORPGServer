package org.game.core.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameThread;
import org.game.core.rpc.ToPoint;
import org.game.player.PlayerObject;
import org.game.player.service.PlayerService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人物查找
 */
public class PlayerLookup {

    public static final Logger logger = LogManager.getLogger(PlayerLookup.class);

    private static final Map<Long, PlayerObject> playerIdMap = new ConcurrentHashMap<>();
    private static final Map<String, PlayerObject> accountMap = new ConcurrentHashMap<>();

    public static void add(long playerId, PlayerObject playerObj) {
        // 添加ID映射
        playerIdMap.put(playerId, playerObj);
        // 添加账号映射
        accountMap.put(playerObj.getAccount(), playerObj);
    }

    public static void remove(long playerId) {
        PlayerObject playerObject = playerIdMap.get(playerId);
        if (playerObject != null) {
            playerIdMap.remove(playerId);
            accountMap.remove(playerObject.getAccount());
        }
    }

    /**
     * 获取玩家线程名称
     */
    public static String getPlayerThreadName(long playerId) {
        PlayerObject playerObject = playerIdMap.get(playerId);
        if (playerObject != null) {
            return playerObject.getPlayerPoint().getGameThreadName();
        }
        return null;
    }

    public static void KickPlayerByAccount(String account) {
        PlayerObject playerObject = accountMap.get(account);
        if (playerObject != null) {
            String playerThreadName = getPlayerThreadName(playerObject.getPlayerId());
            GameThread humanThread = GameProcess.getGameThread(playerThreadName);
            if (humanThread != null) {
                humanThread.runTask(() -> {
                    PlayerService humanService = (PlayerService)humanThread.getGameService(String.valueOf(playerObject.getPlayerId()));
                    if (humanService != null) {
                        PlayerObject humanObj = humanService.getPlayerObj();
                        humanObj.disconnect();
                        logger.info("KickPlayerByAccount: {}", humanObj);
                    }
                });
            }
        }
    }

}
