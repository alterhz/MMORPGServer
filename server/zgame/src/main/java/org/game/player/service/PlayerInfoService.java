package org.game.player.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.PlayerServiceBase;
import org.game.player.PlayerObject;
import org.game.player.PlayerStateEnum;
import org.game.player.module.MyStruct;
import org.game.player.rpc.IPlayerInfoService;
import org.game.core.utils.Vector3;

import java.util.concurrent.CompletableFuture;

public class PlayerInfoService extends PlayerServiceBase implements IPlayerInfoService {

    public static final Logger logger = LogManager.getLogger(PlayerInfoService.class);

    public PlayerInfoService(PlayerObject humanObj) {
        super(humanObj);
    }

    @Override
    public CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct) {
        long id = playerObj.getPlayerId();
        return CompletableFuture.completedFuture("playerId=" + id);
    }

    @Override
    public void savePosition(Vector3 position) {
        logger.info("savePosition: {}", position);

        // 保存完毕，更新销毁状态
        getPlayerObj().setState(PlayerStateEnum.DESTROY);
    }


}
