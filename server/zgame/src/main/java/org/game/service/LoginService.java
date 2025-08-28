package org.game.service;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.HumanDBManager;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.db.QuerySubscriber;
import org.game.core.human.HumanThread;
import org.game.core.net.ClientPeriod;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.dao.HumanDB;
import org.game.proto.*;
import org.game.rpc.IClientService;
import org.game.rpc.ILoginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginService extends GameServiceBase implements ILoginService {

    public static final Logger logger = LogManager.getLogger(LoginService.class);

    /**
     * 账号登录信息,key:account
     */
    private final Map<String, LoginInfo> accountLoginInfoMap = new java.util.HashMap<>();

    /**
     * 登录信息,key:clientID
     */
    private final Map<Long, LoginInfo> loginInfoMap = new java.util.HashMap<>();

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
    public void hotfix(Param param) {
        logger.info("LoginService 热修复: param={}", param);
    }

    @Override
    public void dispatch(Message message, ToPoint fromPoint) {
        switch (message.getProtoID()) {
            case Proto.CS_LOGIN:
                CSLogin(message, fromPoint);
                break;
            case Proto.CS_QUERY_HUMANS:
                CSQueryHumans(message, fromPoint);
                break;
            case Proto.CS_SELECT_HUMAN:
                CSSelectHuman(message, fromPoint);
                break;
            default:
                logger.warn("LoginService 接收到未知消息: {}", message);
                break;
        }
    }

    private void CSSelectHuman(Message message, ToPoint fromPoint) {
        logger.info("接收到消息CS_SELECT_HUMAN: {}", message);
        long clientID = NumberUtils.toLong(fromPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("选择角色，loginInfo == null: clientID={}", clientID);
            return;
        }
        if (loginInfo.loginPeriod != LoginPeriod.QUERY_HUMANS) {
            logger.error("选择角色，不在查询角色阶段: clientID={}, loginPeriod={}", clientID, loginInfo.loginPeriod);
            return;
        }
        loginInfo.loginPeriod = LoginPeriod.SELECT_HUMAN;

        CSSelectHuman csSelectHuman = message.getJsonObject(CSSelectHuman.class);
        String humanId = csSelectHuman.getHumanId();

        // 是否存在HumanId
        if (!loginInfo.humans.contains(humanId)) {
            logger.error("选择的角色不存在: humanId={}", humanId);
            SCSelectHuman scSelectHuman = new SCSelectHuman();
            scSelectHuman.setCode(1);
            scSelectHuman.setMessage("选择失败");
            sendProto(fromPoint, Proto.SC_SELECT_HUMAN, scSelectHuman);
            return;
        }

        MongoDBAsyncClient.getCollection("humans", HumanDB.class)
                .find(Filters.eq("id", humanId))
                .subscribe(new QuerySubscriber<>() {
                    @Override
                    protected void onLoadDB(List<HumanDB> humanDBS) {
                        if (!humanDBS.isEmpty()) {
                            HumanDB selectHumanDB = humanDBS.get(0);
                            HumanThread.createHumanObject(selectHumanDB);
                        } else {
                            logger.error("选择的角色不存在: humanId={}", humanId);
                            SCSelectHuman scSelectHuman = new SCSelectHuman();
                            scSelectHuman.setCode(1);
                            scSelectHuman.setMessage("选择失败");
                            sendProto(fromPoint, Proto.SC_SELECT_HUMAN, scSelectHuman);
                        }
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.error("查询角色失败: {}", errMessage);

                        // 角色列表查询失败
                        SCQueryHumans scQueryHumans = new SCQueryHumans();
                        scQueryHumans.setCode(1);
                        scQueryHumans.setMessage("查询失败");
                        sendProto(fromPoint, Proto.SC_QUERY_HUMANS, scQueryHumans);
                    }
                });
    }

    private void CSQueryHumans(Message message, ToPoint fromPoint) {
        logger.info("接收到消息CS_QUERY_HUMANS: {}", message);
        long clientID = NumberUtils.toLong(fromPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("获取角色列表，loginInfo == null: clientID={}", clientID);
            return;
        }

        if (loginInfo.loginPeriod != LoginPeriod.LOGIN) {
            logger.error("获取角色列表，不在登录阶段: clientID={}, loginPeriod={}", clientID, loginInfo.loginPeriod);
            return;
        }
        // 记录请求角色列表
        loginInfo.loginPeriod = LoginPeriod.QUERY_HUMANS;

        String account = loginInfo.account;
        MongoDBAsyncClient.getCollection("humans", HumanDB.class)
                .find(Filters.eq("account", account))
                .subscribe(new QuerySubscriber<>() {
                    @Override
                    protected void onLoadDB(List<HumanDB> humanDBS) {
                        List<HumanInfo> humanList = new ArrayList<>();
                        for (HumanDB humanDB : humanDBS) {
                            // 创建角色信息对象
                            String hexHumanId = humanDB.getId().toHexString();

                            HumanInfo humanInfo = new HumanInfo();
                            humanInfo.setId(hexHumanId);
                            humanInfo.setName(humanDB.getName());
                            // 这里暂时将职业字段设置为默认值，因为在HumanDB中没有找到职业字段
                            humanInfo.setProfession("未知职业");
                            humanList.add(humanInfo);

                            // 记录角色列表ID
                            loginInfo.humans.add(hexHumanId);
                        }

                        SCQueryHumans scQueryHumans = new SCQueryHumans();
                        scQueryHumans.setCode(0);
                        scQueryHumans.setHumanList(humanList);
                        scQueryHumans.setMessage("查询成功");

                        sendProto(fromPoint, Proto.SC_QUERY_HUMANS, scQueryHumans);
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.error("查询角色列表失败: {}", errMessage);

                        // 角色列表查询失败
                        SCQueryHumans scQueryHumans = new SCQueryHumans();
                        scQueryHumans.setCode(1);
                        scQueryHumans.setMessage("查询失败");
                        sendProto(fromPoint, Proto.SC_QUERY_HUMANS, scQueryHumans);
                    }
                });
    }

    private void CSLogin(Message message, ToPoint fromPoint) {
        logger.info("接收到消息CS_LOGIN: {}", message);
        long clientID = NumberUtils.toLong(fromPoint.getGameServiceName());

        CSLogin csLogin = message.getJsonObject(CSLogin.class);
        String account = csLogin.getAccount();

        // 账号已登录
        LoginInfo existLoginInfo = accountLoginInfoMap.get(account);
        if (existLoginInfo != null && existLoginInfo.clientPoint != fromPoint) {
            // 踢掉以前账号的登录
            accountLoginInfoMap.remove(account);
            loginInfoMap.remove(clientID);
            IClientService clientService = ReferenceFactory.getProxy(IClientService.class, existLoginInfo.clientPoint);
            clientService.Disconnect();
            return;
        }

        // 记录账号登录信息
        LoginInfo loginInfo = new LoginInfo(account, fromPoint);
        accountLoginInfoMap.put(account, loginInfo);
        // 获取客户端ID
        loginInfoMap.put(clientID, loginInfo);

        // 切换到选择角色阶段
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, fromPoint);
        clientService.changePeriod(ClientPeriod.SELECT_HUMAN);

        SCLogin scLogin = new SCLogin();
        scLogin.setCode(0);
        scLogin.setMessage("登录成功");
        sendProto(fromPoint, Proto.SC_LOGIN, scLogin);
    }

    private <T> void sendProto(ToPoint clientPoint, int protoID, T proto) {
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        clientService.sendMessage(Message.createMessage(protoID, proto));
    }

    enum LoginPeriod {
        LOGIN,
        QUERY_HUMANS,
        SELECT_HUMAN,
    }

    static class LoginInfo {
        String account;
        ToPoint clientPoint;
        LoginPeriod loginPeriod = LoginPeriod.LOGIN;
        // 账号对应的HumanDB列表
        List<String> humans = new ArrayList<>();

        LoginInfo(String account, ToPoint clientPoint) {
            this.account = account;
            this.clientPoint = clientPoint;
        }
    }

}