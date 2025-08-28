package org.game.rpc;

import org.game.core.Param;
import org.game.core.net.ClientPeriod;
import org.game.core.net.Message;
import org.game.core.rpc.RPCProxy;
import org.game.core.rpc.ToPoint;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IClientService {
    void hotfix(Param param);

    void changePeriod(ClientPeriod period);

    void setHumanToPoint(String humanId, ToPoint humanPoint);

    void sendMessage(Message message);

    void Disconnect();

}
