package org.game.global.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

/**
 * 服务器服务接口，用于处理服务器级别的全局操作，如ID分配等
 */
@RPCProxy()
public interface IServerService {
    /**
     * 分配一个新的唯一ID
     */
    void updateId();

    /**
     * 热修复
     * 
     * @param param 参数
     */
    void hotfix(Param param);
}