package org.game.human.rpc;

import org.game.core.Param;
import org.game.core.net.Message;
import org.game.core.rpc.RPCProxy;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IHumanService {
    /**
     * 热更新
     */
    void hotfix(Param param);

    /**
     * 转发消息
     */
    void dispatchProto(Message message);

    /**
     * 转发rpc调用
     */
    public static final String DISPATCH_METHOD_NAME = "dispatchRPC";
    /**
     * 转发rpc调用
     */
    CompletableFuture<Object> dispatchRPC(String hModService, String methodName, List<Object> parameters, List<String> parameterTypes);

}
