package org.game.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.rpc.ILoginService;

import java.util.concurrent.CompletableFuture;

public class LoginService extends GameServiceBase implements ILoginService {

    public static final Logger logger = LogManager.getLogger(LoginService.class);

    public LoginService(String name) {
        super(name);
    }

    @Override
    public void init() {
        // 初始化逻辑
        logger.info("LoginService 初始化");
    }

    @Override
    public void startup() {
        logger.info("LoginService 启动");
    }

    @Override
    public void pulse(long now) {
        // 心跳逻辑
    }

    @Override
    public void destroy() {
        // 销毁逻辑
        logger.info("LoginService 销毁");
    }

    @Override
    public CompletableFuture<Boolean> login(String account, String password) {
        logger.info("用户登录请求: account={}, password=***", account);
        // 这里应该实现实际的登录逻辑
        // 例如验证账户和密码是否匹配
        boolean loginSuccess = validateCredentials(account, password);
        return CompletableFuture.completedFuture(loginSuccess);
    }

    @Override
    public void hotfix(Param param) {
        logger.info("LoginService 热修复: param={}", param);
    }

    /**
     * 验证用户凭据
     * @param account 账户名
     * @param password 密码
     * @return 验证结果
     */
    private boolean validateCredentials(String account, String password) {
        // 实际项目中这里应该查询数据库或调用认证服务
        // 这里简单示例：用户名为"test"，密码为"123456"时登录成功
        return "test".equals(account) && "123456".equals(password);
    }
}