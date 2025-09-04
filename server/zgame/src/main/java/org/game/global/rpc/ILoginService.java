package org.game.global.rpc;

import org.game.core.Param;
import org.game.core.net.Message;
import org.game.core.rpc.RPCProxy;
import org.game.core.rpc.ToPoint;

@RPCProxy
public interface ILoginService {

    /**
     * 热修复
     */
    void hotfix(Param param);

    /**
     * 分发消息
     */
    void dispatch(Message message, ToPoint fromPoint);
}