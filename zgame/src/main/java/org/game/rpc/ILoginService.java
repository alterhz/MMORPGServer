package org.game.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.concurrent.CompletableFuture;

@RPCProxy()
public interface ILoginService {
    /**
     * 用户登录
     * @param account 账户名
     * @param password 密码
     * @return 登录结果
     */
    CompletableFuture<Boolean> login(String account, String password);
    
    /**
     * 热修复
     */
    void hotfix(Param param);
}