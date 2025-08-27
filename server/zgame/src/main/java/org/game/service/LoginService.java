package org.game.service;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.db.QuerySubscriber;
import org.game.core.human.HumanThread;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.dao.HumanDB;
import org.game.proto.ResponseMessage;
import org.game.rpc.IClientService;
import org.game.rpc.ILoginService;

import java.util.List;

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
    public void login(String account, String password, ToPoint fromPoint) {
        logger.info("用户登录请求: account={}, password=***, fromPoint={}", account, fromPoint);

        MongoCollection<HumanDB> humans = MongoDBAsyncClient.getCollection("humans", HumanDB.class);

        // 查找账号为account的HumanDB
        humans.find(Filters.eq("account", account)).first().subscribe(new QuerySubscriber<>() {
            @Override
            protected void onLoadDB(List<HumanDB> humanDBS) {
                ResponseMessage responseMessage;
                if (!humanDBS.isEmpty()) {
                    // 登录成功
                    responseMessage = ResponseMessage.success("human count=" + humanDBS.size());

                    HumanDB humanDB = humanDBS.get(0);
                    HumanThread.createHumanObject(humanDB.getAccount());
                } else {
                    // 登录失败
                    responseMessage = ResponseMessage.error(-1, "用户：" + account + "，不存在！");
                }

                IClientService clientService = ReferenceFactory.getProxy(IClientService.class, fromPoint);
                clientService.sendMessage(Message.createMessage(1002, responseMessage));

                logger.debug("发送登录结果: {}", responseMessage);
            }
        });
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