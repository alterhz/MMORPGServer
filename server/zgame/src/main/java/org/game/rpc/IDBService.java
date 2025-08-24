package org.game.rpc;

import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.concurrent.CompletableFuture;

/**
 * 数据库服务接口，负责处理数据库相关操作
 * 继承自RPC服务，具备远程调用能力
 */
@RPCProxy()
public interface IDBService {

    /**
     * 热修复
     * 
     * @param param 参数
     */
    void hotfix(Param param);

    /**
     * 查询
     *
     * @param param 参数
     * @return 结果
     */
    CompletableFuture<Object> query(Param param);
}