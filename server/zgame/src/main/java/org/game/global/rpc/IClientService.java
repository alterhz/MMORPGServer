package org.game.global.rpc;

import org.game.core.Param;
import org.game.core.net.ClientPeriod;
import org.game.core.net.Message;
import org.game.core.rpc.RPCProxy;
import org.game.core.rpc.ToPoint;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IClientService {
    void hotfix(Param param);

    void changePeriod(ClientPeriod period);

    void setPlayerPoint(String humanId, ToPoint humanPoint);

    void setStageHumanToPoint(ToPoint stageHumanPoint);

    void sendMessage(Message message);

    void Disconnect();

}
