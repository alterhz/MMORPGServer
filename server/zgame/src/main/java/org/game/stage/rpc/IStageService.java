package org.game.stage.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.concurrent.CompletableFuture;


/**
 * 场景服务
 * <p>每个线程一个，手动启动</p>
 */
@RPCProxy(startupType=RPCProxy.StartupType.MANUAL)
public interface IStageService {
    /**
     * 创建普通场景
     * @param stageSn 场景配置SN
     * @return 场景信息
     */
    CompletableFuture<Param> createCommonStage(int stageSn, long stageId);

    /**
     * 热修复
     * @param param 热修复参数
     */
    void hotfix(Param param);
}