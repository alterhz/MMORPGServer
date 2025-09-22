package org.game.player.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.core.rpc.ToPoint;

import java.util.concurrent.CompletableFuture;

/**
 * 玩家服务
 */
@HumanRPCProxy
public interface IPlayerService {
    /**
     * 热更
     */
    void hotfix(String token);

    /**
     * 重连
     *
     * @return
     */
    CompletableFuture<Boolean> reconnect(String token, ToPoint clientPoint);
}
