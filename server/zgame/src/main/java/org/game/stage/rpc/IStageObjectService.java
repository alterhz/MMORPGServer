package org.game.stage.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;
import org.game.stage.human.HumanObjectData;

@RPCProxy(startupType = RPCProxy.StartupType.MANUAL)
public interface IStageObjectService {
    /**
     * 热修复
     * @param param 热修复参数
     */
    void hotfix(Param param);

    void registerStageHuman(HumanObjectData humanObjectData);
}