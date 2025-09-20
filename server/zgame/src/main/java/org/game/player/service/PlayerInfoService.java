package org.game.player.service;

import org.game.core.rpc.HumanServiceBase;
import org.game.player.PlayerObject;
import org.game.player.module.MyStruct;
import org.game.player.rpc.IPlayerInfoService;

import java.util.concurrent.CompletableFuture;

public class PlayerInfoService extends HumanServiceBase implements IPlayerInfoService {
    public PlayerInfoService(PlayerObject humanObj) {
        super(humanObj);
    }

    @Override
    public CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct) {
        long id = humanObj.getId();
        return CompletableFuture.completedFuture("playerId=" + id);
    }
}
