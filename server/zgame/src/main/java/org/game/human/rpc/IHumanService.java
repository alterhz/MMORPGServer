package org.game.human.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IHumanService {
    /**
     * 热更新
     */
    void hotfix(Param param);

    CompletableFuture<Object> dispatchMethod(String hModService, String methodName, List<Object> parameters, List<String> parameterTypes);

}
