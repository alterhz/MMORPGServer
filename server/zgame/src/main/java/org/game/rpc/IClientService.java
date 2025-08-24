package org.game.rpc;

import org.game.core.Param;
import org.game.core.net.Message;
import org.game.core.rpc.RPCProxy;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IClientService {
    void hotfix(Param param);

    void sendMessage(Message message);
}
