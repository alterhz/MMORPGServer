package org.game.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

@RPCProxy()
public interface IStageGlobalService {
    void hotfix(Param param);
}