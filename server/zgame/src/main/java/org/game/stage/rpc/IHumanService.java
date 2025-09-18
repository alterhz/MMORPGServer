package org.game.stage.rpc;

import org.game.core.Param;
import org.game.core.net.Message;
import org.game.core.rpc.RPCProxy;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IHumanService {
    /**
     * 热修复
     * @param param 热修复参数
     */
    void hotfix(Param param);

    /**
     * 分发消息
     * @param message 消息
     */
    void dispatchProto(Message message);
}