package org.game.global.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.db.QuerySubscriber;
import org.game.core.human.HumanLookup;
import org.game.core.human.HumanThread;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.core.net.ClientPeriod;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.dao.HumanDB;
import org.game.human.module.MyStruct;
import org.game.human.rpc.IHumanInfoService;
import org.game.proto.common.HumanInfo;
import org.game.proto.login.*;
import org.game.global.rpc.IClientService;
import org.game.global.rpc.ILoginService;

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

    /**
     * 登录协议分发器
     */
    private final LoginDispatcher loginDispatcher = new LoginDispatcher();

    public LoginService(String name) {
        super(name);
    }

    @Override
    public void init() {
        // 初始化逻辑
        logger.info("LoginService 初始化");
        loginDispatcher.init();
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
        loginDispatcher.dispatch(String.valueOf(message.getProtoID()), this, message, fromPoint);
    }

    // CS_TEST
    @ProtoListener(CSTest.class)
    private void CSTest(Message message, ToPoint clientPoint) {
        logger.info("接收到消息CS_TEST: {}", message);
        long clientID = NumberUtils.toLong(clientPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("CSTest，loginInfo == null: clientID={}", clientID);
            return;
        }
        String humanId = loginInfo.humans.get(0);
        IHumanInfoService humanInfoService = ReferenceFactory.getHumanProxy(IHumanInfoService.class, humanId);
        MyStruct myStruct = new MyStruct();
        myStruct.setId(1);
        myStruct.setName("张三");
        myStruct.setSex(true);
        myStruct.setDesc("测试");
        humanInfoService.getInfo(33, "dfsd", myStruct).thenAccept(humanInfo -> {
            logger.info("调用HumanRPC，成功: {}", humanInfo);
        });
        SCTest scTest = new SCTest();
        sendProto(clientPoint, scTest);
    }

    @ProtoListener(CSCreateHuman.class)
    private void CSCreateHuman(Message message, ToPoint clientPoint) {
        logger.info("接收到消息CS_CREATE_HUMAN: {}", message);
        long clientID = NumberUtils.toLong(clientPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("创建角色，loginInfo == null: clientID={}", clientID);
            return;
        }
        if (loginInfo.loginPeriod != LoginPeriod.QUERY_HUMANS) {
            logger.error("创建角色，不在查询角色阶段: clientID={}, loginPeriod={}", clientID, loginInfo.loginPeriod);
            return;
        }
        loginInfo.loginPeriod = LoginPeriod.CREATE_HUMAN;

        CSCreateHuman csCreateHuman = message.getJsonObject(CSCreateHuman.class);

        // TODO 创建角色
        HumanDB humanDB = new HumanDB();
        humanDB.setId(null);
        humanDB.setAccount(loginInfo.account);
        humanDB.setName(csCreateHuman.getName());

        MongoDBAsyncClient.getCollection(HumanDB.class)
                .insertOne(humanDB)
                .subscribe(new QuerySubscriber<>() {
                    @Override
                    protected void onLoadDB(List<InsertOneResult> dbCollections) {
                        BsonValue insertedId = dbCollections.get(0).getInsertedId();
                        logger.info("创建角色成功: insertedId={}", insertedId);
                        SCCreateHuman scCreateHuman = new SCCreateHuman();
                        scCreateHuman.setCode(0);
                        scCreateHuman.setHumanId(insertedId.asObjectId().getValue().toHexString());
                        scCreateHuman.setSuccess(true);
                        sendProto(clientPoint, scCreateHuman);

                        HumanThread.loadHumanObject(humanDB, clientPoint);
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.info("创建角色失败: {}", errMessage);
                    }
                });

    }

    @ProtoListener(CSSelectHuman.class)
    private void CSSelectHuman(Message message, ToPoint clientPoint) {
        logger.info("接收到消息CS_SELECT_HUMAN: {}", message);
        long clientID = NumberUtils.toLong(clientPoint.getGameServiceName());
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
            sendProto(clientPoint, scSelectHuman);
            return;
        }

        MongoDBAsyncClient.getCollection(HumanDB.class)
                .find(Filters.eq("_id", new ObjectId(humanId)))
                .subscribe(new QuerySubscriber<>() {
                    @Override
                    protected void onLoadDB(List<HumanDB> humanDBS) {
                        if (!humanDBS.isEmpty()) {
                            HumanDB selectHumanDB = humanDBS.get(0);
                            HumanThread.loadHumanObject(selectHumanDB, clientPoint);

                            // 选择角色成功
                            SCSelectHuman scSelectHuman = new SCSelectHuman(0, "选择角色成功");
                            sendProto(clientPoint, scSelectHuman);
                        } else {
                            logger.error("选择的角色不存在: humanId={}", humanId);
                            SCSelectHuman scSelectHuman = new SCSelectHuman();
                            scSelectHuman.setCode(1);
                            scSelectHuman.setMessage("选择失败");
                            sendProto(clientPoint, scSelectHuman);
                        }
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.error("查询角色失败: {}", errMessage);

                        // 角色列表查询失败
                        SCQueryHumans scQueryHumans = new SCQueryHumans();
                        scQueryHumans.setCode(1);
                        scQueryHumans.setMessage("查询失败");
                        sendProto(clientPoint, scQueryHumans);
                    }
                });
    }

    @ProtoListener(CSQueryHumans.class)
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
        MongoDBAsyncClient.getCollection(HumanDB.class)
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

                        sendProto(fromPoint, scQueryHumans);
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.error("查询角色列表失败: {}", errMessage);

                        // 角色列表查询失败
                        SCQueryHumans scQueryHumans = new SCQueryHumans();
                        scQueryHumans.setCode(1);
                        scQueryHumans.setMessage("查询失败");
                        sendProto(fromPoint, scQueryHumans);
                    }
                });
    }

    @ProtoListener(CSLogin.class)
    private void CSLogin(Message message, ToPoint fromPoint) {
        logger.info("接收到消息CS_LOGIN: {}", message);
        long clientID = NumberUtils.toLong(fromPoint.getGameServiceName());

        CSLogin csLogin = message.getJsonObject(CSLogin.class);
        String account = csLogin.getAccount();

        // 账号不能为空或者空格
        if (StringUtils.isBlank(account)) {
            logger.error("账号不能为空或者空格: account={}", account);
            SCLogin scLogin = new SCLogin();
            scLogin.setCode(1);
            scLogin.setMessage("登录失败：账号不能为空");
            sendProto(fromPoint, scLogin);
            return;
        }

        // 账号已登录
        LoginInfo existLoginInfo = accountLoginInfoMap.get(account);
        if (existLoginInfo != null && existLoginInfo.clientPoint != fromPoint) {
            // 踢掉以前账号的登录
            accountLoginInfoMap.remove(account);
            loginInfoMap.remove(clientID);
            IClientService clientService = ReferenceFactory.getProxy(IClientService.class, existLoginInfo.clientPoint);
            clientService.Disconnect();
        }

        // 踢掉在线的角色
        HumanLookup.KickHumanByAccount(account);

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
        sendProto(fromPoint, scLogin);
    }

    private <T> void sendProto(ToPoint clientPoint, T proto) {
        Integer protoID = ProtoScanner.getProtoID(proto.getClass());
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        clientService.sendMessage(Message.createMessage(protoID, proto));
    }

    enum LoginPeriod {
        LOGIN,
        QUERY_HUMANS,
        SELECT_HUMAN,
        CREATE_HUMAN,
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