package org.game.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;
import org.game.core.rpc.ToPoint;

@RPCProxy()
public interface ILoginService {
    /**
     * 用户登录
     *
     * @param account   账户名
     * @param password  密码
     * @param fromPoint
     * @return 登录结果
     */
    void login(String account, String password, ToPoint fromPoint);
    
    /**
     * 热修复
     */
    void hotfix(Param param);
}