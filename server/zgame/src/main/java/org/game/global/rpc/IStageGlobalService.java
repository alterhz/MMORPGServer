package org.game.global.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.concurrent.CompletableFuture;

@RPCProxy()
public interface IStageGlobalService {
    void hotfix(Param param);

    /**
     * 获取场景信息
     * @param stageSn 场景配置SN
     * @param allocType 分配类型
     * @return 场景信息
     */
    CompletableFuture<Param> getStageInfo(int stageSn, int allocType);

    /**
     * 玩家进入场景
     * @param stageId 场景ID
     */
    void humanEnter(long stageId);

    /**
     * 玩家离开场景
     * @param stageId 场景ID
     */
    void humanLeave(long stageId);

    /**
     * 回收测试 - 回收空闲场景
     */
    void recycleTest();
}