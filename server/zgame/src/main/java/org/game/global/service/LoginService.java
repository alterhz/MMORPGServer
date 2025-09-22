package org.game.global.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.game.config.MyConfig;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.db.MongoDBSyncClient;
import org.game.core.db.QuerySubscriber;
import org.game.core.human.IdAllocator;
import org.game.core.human.PlayerLookup;
import org.game.core.human.PlayerThread;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.core.net.ClientPeriod;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.dao.IdAllocatorDB;
import org.game.dao.PlayerDB;
import org.game.dao.ServerDB;
import org.game.player.module.MyStruct;
import org.game.player.rpc.IPlayerInfoService;
import org.game.proto.login.*;
import org.game.global.rpc.IClientService;
import org.game.global.rpc.ILoginService;

import java.util.ArrayList;
import java.util.HashMap;
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

    // ID分配器
    public static IdAllocator playerIdAllocator;

    private IdAllocatorDB playerIdAllocatorDB;

    public LoginService(String name) {
        super(name);
    }

    @Override
    public void init() {
        // 初始化逻辑
        logger.info("LoginService 初始化");
        loginDispatcher.init();

        MongoCollection<IdAllocatorDB> idAllocatorDBCollection = MongoDBSyncClient.getOrCreateCollection(IdAllocatorDB.class);
        IdAllocatorDB loadIdAllocatorDB = idAllocatorDBCollection.find().first();
        if (loadIdAllocatorDB == null) {
            // 创建表
            IdAllocatorDB newIdAllocatorDB = new IdAllocatorDB(1L);
            ObjectId objectId = MongoDBSyncClient.insertOne(newIdAllocatorDB);
            newIdAllocatorDB.setId(objectId);
            this.playerIdAllocatorDB = newIdAllocatorDB;
        } else {
            // 获取表信息
            logger.info("载入IdAllocatorDB信息: {}", loadIdAllocatorDB);
            this.playerIdAllocatorDB = loadIdAllocatorDB;
        }

        // 初始化ID分配器，使用serverId=1，起始序号为0
        int serverId = MyConfig.getConfig().getServer().getServerId();
        playerIdAllocator = new IdAllocator(serverId, playerIdAllocatorDB.getCurrentSequence());
    }

    @Override
    public void startup() {
        logger.info("LoginService 启动");
    }

    @Override
    public void onPulse(long now) {
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
        PlayerDB playerDB = loginInfo.players.values().iterator().next();
        IPlayerInfoService PlayerInfoService = ReferenceFactory.getPlayerProxy(IPlayerInfoService.class, playerDB.getPlayerId());
        MyStruct myStruct = new MyStruct();
        myStruct.setId(1);
        myStruct.setName("张三");
        myStruct.setSex(true);
        myStruct.setDesc("测试");
        PlayerInfoService.getInfo(33, "dfsd", myStruct).whenComplete((s, throwable) -> {
            if (throwable != null) {
                logger.error("CSTest，PlayerInfoService.getInfo: ", throwable);
                return;
            }
            logger.info("CSTest，PlayerInfoService.getInfo: {}", s);
        });
        SCTest scTest = new SCTest();
        scTest.setMessage("登录成功测试");
        sendProto(clientPoint, scTest);
    }

    @ProtoListener(CSCreatePlayer.class)
    private void CSCreatePlayer(Message message, ToPoint clientPoint) {
        logger.info("接收到消息CS_CREATE_PLAYER: {}", message);
        long clientID = NumberUtils.toLong(clientPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("创建角色，loginInfo == null: clientID={}", clientID);
            return;
        }
        if (loginInfo.loginPeriod != LoginPeriod.QUERY_PlayerS) {
            logger.error("创建角色，不在查询角色阶段: clientID={}, loginPeriod={}", clientID, loginInfo.loginPeriod);
            return;
        }
        loginInfo.loginPeriod = LoginPeriod.CREATE_Player;

        CSCreatePlayer csCreatePlayer = message.getProto(CSCreatePlayer.class);

        long allocHumanId = playerIdAllocator.allocateId();
        playerIdAllocatorDB.setCurrentSequence(allocHumanId);
        savePlayerIdAllocatorDB(playerIdAllocatorDB);

        // TODO 创建角色
        PlayerDB playerDB = new PlayerDB();
        playerDB.setId(null);
        playerDB.setPlayerId(allocHumanId);
        playerDB.setAccount(loginInfo.account);
        playerDB.setName(csCreatePlayer.getName());


        MongoDBAsyncClient.getCollection(PlayerDB.class)
                .insertOne(playerDB)
                .subscribe(new QuerySubscriber<>() {
                    @Override
                    protected void onLoadDB(List<InsertOneResult> dbCollections) {
                        BsonValue insertedId = dbCollections.get(0).getInsertedId();
                        playerDB.setId(insertedId.asObjectId().getValue());
                        logger.info("创建角色成功: insertedId={}", insertedId);
                        SCCreatePlayer scCreatePlayer = new SCCreatePlayer();
                        scCreatePlayer.setCode(0);
                        scCreatePlayer.setPlayerId(playerDB.getPlayerId());
                        scCreatePlayer.setSuccess(true);
                        sendProto(clientPoint, scCreatePlayer);

                        PlayerThread.loadPlayerObj(playerDB, clientPoint);
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.info("创建角色失败: {}", errMessage);
                    }
                });

    }

    private void savePlayerIdAllocatorDB(IdAllocatorDB playerIdAllocatorDB) {
        // 保存到数据库
        MongoDBAsyncClient.getCollection(ServerDB.class).updateOne(
                        Filters.eq("_id", playerIdAllocatorDB.getId()),
                        Updates.set("currentSequence", playerIdAllocatorDB.getCurrentSequence()))
                .subscribe(new MongoDBAsyncClient.UpdateSubscriber());
        logger.info("保存ID分配器信息: {}", playerIdAllocator.getCurrentSequence());
    }

    /**
     * 删除角色
     */
    @ProtoListener(CSDeletePlayer.class)
    private void CSDeletePlayer(Message message, ToPoint clientPoint) {
        logger.info("接收到消息CS_DELETE_Player: {}", message);
        long clientID = NumberUtils.toLong(clientPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("删除角色，loginInfo == null: clientID={}", clientID);
            return;
        }
        if (loginInfo.loginPeriod != LoginPeriod.QUERY_PlayerS) {
            logger.error("删除角色, 不在选择角色阶段: clientID={}, loginPeriod={}", clientID, loginInfo.loginPeriod);
            return;
        }

        CSDeletePlayer csDeletePlayer = message.getProto(CSDeletePlayer.class);
        long playerId = csDeletePlayer.getPlayerId();

        // 判断是否存在这个角色
        if (!loginInfo.players.containsKey(playerId)) {
            logger.error("删除角色不存在: playerId={}", playerId);
            SCDeletePlayer scDeletePlayerResp = new SCDeletePlayer();
            scDeletePlayerResp.setCode(1);
            scDeletePlayerResp.setPlayerId(playerId);
            scDeletePlayerResp.setMessage("删除角色不存在");
            sendProto(clientPoint, scDeletePlayerResp);
            return;
        }

        PlayerDB removePlayerDB = loginInfo.players.remove(playerId);

        // 根据角色ID删除角色
        MongoDBAsyncClient.getCollection(PlayerDB.class)
                .deleteOne(Filters.eq("_id", removePlayerDB.getId()))
                .subscribe(new QuerySubscriber<>(Long.MAX_VALUE) {
                    @Override
                    protected void onLoadDB(List<DeleteResult> dbCollections) {
                        if (dbCollections.isEmpty() || dbCollections.get(0).getDeletedCount() == 0) {
                            // 角色不存在
                            logger.error("DB删除角色不存在: playerId={}", playerId);
                            SCDeletePlayer scDeletePlayer = new SCDeletePlayer();
                            scDeletePlayer.setCode(1);
                            scDeletePlayer.setPlayerId(playerId);
                            scDeletePlayer.setMessage("删除角色失败");
                            sendProto(clientPoint, scDeletePlayer);
                        } else {
                            // 角色删除成功
                            SCDeletePlayer scDeletePlayer = new SCDeletePlayer();
                            scDeletePlayer.setCode(0);
                            scDeletePlayer.setPlayerId(playerId);
                            scDeletePlayer.setMessage("删除角色成功");
                            sendProto(clientPoint, scDeletePlayer);
                        }
                    }

                    @Override
                    protected void onError(String errMessage) {
                        SCDeletePlayer scDeletePlayer = new SCDeletePlayer();
                        scDeletePlayer.setCode(2);
                        scDeletePlayer.setPlayerId(playerId);
                        scDeletePlayer.setMessage("删除角色异常: " + errMessage);
                        sendProto(clientPoint, scDeletePlayer);
                    }
                });

    }

    @ProtoListener(CSSelectPlayer.class)
    private void CSSelectPlayer(Message message, ToPoint clientPoint) {
        logger.info("接收到消息CS_SELECT_Player: {}", message);
        long clientID = NumberUtils.toLong(clientPoint.getGameServiceName());
        LoginInfo loginInfo = loginInfoMap.get(clientID);
        if (loginInfo == null) {
            logger.error("选择角色，loginInfo == null: clientID={}", clientID);
            return;
        }
        if (loginInfo.loginPeriod != LoginPeriod.QUERY_PlayerS) {
            logger.error("选择角色，不在查询角色阶段: clientID={}, loginPeriod={}", clientID, loginInfo.loginPeriod);
            return;
        }
        loginInfo.loginPeriod = LoginPeriod.SELECT_Player;

        CSSelectPlayer csSelectPlayer = message.getProto(CSSelectPlayer.class);
        long playerId = csSelectPlayer.getPlayerId();

        // 是否存在PlayerId
        if (!loginInfo.players.containsKey(playerId)) {
            logger.error("选择的角色不存在: playerId={}", playerId);
            SCSelectPlayer scSelectPlayer = new SCSelectPlayer();
            scSelectPlayer.setCode(1);
            scSelectPlayer.setMessage("选择失败");
            sendProto(clientPoint, scSelectPlayer);
            return;
        }

        PlayerDB playerDB = loginInfo.players.get(playerId);

        MongoDBAsyncClient.getCollection(PlayerDB.class)
                .find(Filters.eq("_id", playerDB.getId()))
                .subscribe(new QuerySubscriber<>() {
                    @Override
                    protected void onLoadDB(List<PlayerDB> playerDBS) {
                        if (!playerDBS.isEmpty()) {
                            PlayerDB selectPlayerDB = playerDBS.get(0);
                            PlayerThread.loadPlayerObj(selectPlayerDB, clientPoint);

                            // 选择角色成功
                            SCSelectPlayer scSelectPlayer = new SCSelectPlayer();
                            scSelectPlayer.setCode(0);
                            scSelectPlayer.setMessage("选择角色成功");
                            sendProto(clientPoint, scSelectPlayer);
                        } else {
                            logger.error("选择的角色不存在: playerId={}", playerId);
                            SCSelectPlayer scSelectPlayer = new SCSelectPlayer();
                            scSelectPlayer.setCode(1);
                            scSelectPlayer.setMessage("选择失败");
                            sendProto(clientPoint, scSelectPlayer);
                        }
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.error("查询角色失败: {}", errMessage);

                        // 角色列表查询失败
                        SCQueryPlayer scQueryPlayers = new SCQueryPlayer();
                        scQueryPlayers.setCode(1);
                        scQueryPlayers.setMessage("查询失败");
                        sendProto(clientPoint, scQueryPlayers);
                    }
                });
    }

    @ProtoListener(CSQueryPlayer.class)
    private void CSQueryPlayers(Message message, ToPoint fromPoint) {
        logger.info("接收到消息CS_QUERY_PlayerS: {}", message);
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
        loginInfo.loginPeriod = LoginPeriod.QUERY_PlayerS;

        String account = loginInfo.account;
        MongoDBAsyncClient.getCollection(PlayerDB.class)
                .find(Filters.eq("account", account))
                .subscribe(new QuerySubscriber<>(Long.MAX_VALUE) {
                    @Override
                    protected void onLoadDB(List<PlayerDB> playerDBS) {
                        List<Player> PlayerList = new ArrayList<>();
                        for (PlayerDB playerDB : playerDBS) {
                            // 创建角色信息对象
                            long playerId = playerDB.getPlayerId();

                            Player PlayerInfo = new Player();
                            PlayerInfo.setid(playerId);
                            PlayerInfo.setName(playerDB.getName());
                            // 这里暂时将职业字段设置为默认值，因为在PlayerDB中没有找到职业字段
                            PlayerInfo.setProfession("未知职业");
                            PlayerList.add(PlayerInfo);

                            // 记录角色列表ID
                            loginInfo.players.put(playerId, playerDB);
                        }

                        SCQueryPlayer scQueryPlayers = new SCQueryPlayer();
                        scQueryPlayers.setCode(0);
                        scQueryPlayers.setPlayer(PlayerList);
                        scQueryPlayers.setMessage("查询成功");

                        sendProto(fromPoint, scQueryPlayers);
                    }

                    @Override
                    protected void onError(String errMessage) {
                        logger.error("查询角色列表失败: {}", errMessage);

                        // 角色列表查询失败
                        SCQueryPlayer scQueryPlayers = new SCQueryPlayer();
                        scQueryPlayers.setCode(1);
                        scQueryPlayers.setMessage("查询失败");
                        sendProto(fromPoint, scQueryPlayers);
                    }
                });
    }

    @ProtoListener(CSLogin.class)
    private void CSLogin(Message message, ToPoint fromPoint) {
        logger.info("接收到消息CS_LOGIN: {}", message);
        long clientID = NumberUtils.toLong(fromPoint.getGameServiceName());

        CSLogin csLogin = message.getProto(CSLogin.class);
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
        PlayerLookup.KickPlayerByAccount(account);

        // 记录账号登录信息
        LoginInfo loginInfo = new LoginInfo(account, fromPoint);
        accountLoginInfoMap.put(account, loginInfo);
        // 获取客户端ID
        loginInfoMap.put(clientID, loginInfo);

        // 切换到选择角色阶段
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, fromPoint);
        clientService.changePeriod(ClientPeriod.SELECT_PLAYER);

        SCLogin scLogin = new SCLogin();
        scLogin.setCode(0);
        scLogin.setMessage("登录成功");
        sendProto(fromPoint, scLogin);
    }

    private <T> void sendProto(ToPoint clientPoint, T proto) {
        int protoID = ProtoScanner.getProtoID(proto.getClass());
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        clientService.sendMessage(Message.createMessage(protoID, proto));
    }

    enum LoginPeriod {
        LOGIN,
        QUERY_PlayerS,
        SELECT_Player,
        CREATE_Player,
    }

    static class LoginInfo {
        String account;
        ToPoint clientPoint;
        LoginPeriod loginPeriod = LoginPeriod.LOGIN;
        // 账号对应的PlayerDB列表
        Map<Long, PlayerDB> players = new HashMap<>();

        LoginInfo(String account, ToPoint clientPoint) {
            this.account = account;
            this.clientPoint = clientPoint;
        }
    }

}