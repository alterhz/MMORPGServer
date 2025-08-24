package org.game.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IHumanObjectService {
    /**
     * 热更新
     */
    void hotfix(Param param);
}
