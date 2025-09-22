package org.game.player.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.PlayerServiceBase;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.player.PlayerObject;
import org.game.player.rpc.IPlayerService;
import org.game.stage.rpc.IHumanService;

import java.util.concurrent.CompletableFuture;

public class PlayerService extends PlayerServiceBase implements IPlayerService {

    public static final Logger logger = LogManager.getLogger(PlayerService.class);

    public PlayerService(PlayerObject playerObj) {
        super(playerObj);
    }

    @Override
    public void hotfix(String token) {

    }

    @Override
    public CompletableFuture<Boolean> reconnect(String token, ToPoint clientPoint) {
        if (!playerObj.getToken().equalsIgnoreCase(token)) {
            logger.error("HumanObjectService 重连失败: token={}, playerObj={}", token, playerObj);
            return CompletableFuture.completedFuture(false);
        }

        logger.info("HumanObjectService 重连成功: token={}, playerObj={}", token, playerObj);
        playerObj.reconnect(clientPoint);

        // 场景HumanObject重置
        IHumanService humanService = ReferenceFactory.getProxy(IHumanService.class, playerObj.getHumanPoint());
        humanService.reconnect(playerObj.getClientPoint());

        return CompletableFuture.completedFuture(true);
    }

}
