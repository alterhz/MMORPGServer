package org.game.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.concurrent.CompletableFuture;

@RPCProxy()
public interface IHumanGlobalService {
    /**
     * 获取在线人数
     **/
    CompletableFuture<Integer> getHumanOnlineCount(int minLevel);

    /**
     * 测试
     */
    void test();

    /**
     * 热修复
     */
    void hotfix(Param param);
}