package org.game.player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.TimerQueue;
import org.game.core.event.PlayerEventDispatcher;
import org.game.core.event.IEvent;
import org.game.core.player.PlayerModScanner;
import org.game.core.net.Message;
import org.game.core.rpc.*;
import org.game.dao.PlayerDB;
import org.game.core.message.ProtoScanner;
import org.game.global.rpc.IClientService;
import org.game.player.event.OnPlayerLoadComplete;
import org.game.player.event.OnSendToClient;
import org.game.proto.login.SCSendToClientBegin;
import org.game.proto.login.SCSendToClientEnd;
import org.game.stage.rpc.IHumanService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色对象
 */
public class PlayerObject {

    public static final Logger logger = LogManager.getLogger(PlayerObject.class);
    /**
     * 玩家最大掉线可以返回时间：5分钟
     */
    public static final int MAX_DROP_TIME = 5 * 60 * 1000;

    private final long playerId;

    private PlayerStateEnum state = PlayerStateEnum.LOADING;

    /**
     * PlayerService连接点
     */
    private final ToPoint playerPoint;

    /**
     * ClientService连接点
     */
    private ToPoint clientPoint;

    /**
     * 断线时间
     */
    private long disconnectTime;

    /**
     * 登录Token
     */
    private String token;

    /**
     * HumanService连接点
     */
    private ToPoint humanPoint;

    /**
     * Player基础数据
     */
    private final PlayerDB playerDB;

    /**
     * 正在加载的PlayerDB
     */
    private final List<String> loadingPlayerDBs = new ArrayList<>();

    /**
     * PlayerMod模块
     */
    private final Map<Class<?>, PlayerModBase> modBaseMap = new HashMap<>();

    /**
     * HumanServiceBase
     * key: classSimpleName, value: HumanServiceBase
     */
    private final Map<String, PlayerServiceBase> humanServiceBaseMap = new HashMap<>();

    private final TimerQueue timerQueue = new TimerQueue();

    public PlayerObject(PlayerDB playerDB, ToPoint playerPoint) {
        this.playerId = playerDB.getPlayerId();
        this.playerDB = playerDB;
        this.playerPoint = playerPoint;
    }

    public long getPlayerId() {
        return playerId;
    }

    public PlayerStateEnum getState() {
        return state;
    }

    public void setState(PlayerStateEnum state) {
        this.state = state;
    }

    public PlayerDB getPlayerDB() {
        return playerDB;
    }

    // ToPoint连接点

    public ToPoint getPlayerPoint() {
        return playerPoint;
    }

    public void setClientPoint(ToPoint clientPoint) {
        this.clientPoint = clientPoint;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    public ToPoint getHumanPoint() {
        return humanPoint;
    }

    public void setHumanPoint(ToPoint humanPoint) {
        this.humanPoint = humanPoint;
    }

    public void addLoadingHModDB(String hModDB) {
        loadingPlayerDBs.add(hModDB);
        logger.debug("正在加载 {}", hModDB);
    }

    public void removeLoadingHModDB(String hModDB) {
        loadingPlayerDBs.remove(hModDB);
        logger.debug("加载完成 {}", hModDB);
    }

    public void loadHModComplete() {
        if (loadingPlayerDBs.isEmpty()) {
            onLoadingComplete();
        }
    }

    /**
     * 所有HMod加载完成
     */
    protected void onLoadingComplete() {
        fireEvent(new OnPlayerLoadComplete());

        setState(PlayerStateEnum.READY);
        // 随机token
        token = String.valueOf(Math.random());

        syncPlayerPointToClientService();

        // 发送协议
        sendToClientEvent();
    }

    public void reconnect(ToPoint clientPoint) {
        logger.debug("PlayerObject 重连: {}", clientPoint);
        setClientPoint(clientPoint);

        syncPlayerPointToClientService();

        sendToClientEvent();

        // 断线重连
        disconnectTime = 0L;
    }

    private void sendToClientEvent() {
        // 发送协议
        SCSendToClientBegin scSendToClientBegin = new SCSendToClientBegin();
        sendMessage(scSendToClientBegin);

        fireEvent(new OnSendToClient());

        SCSendToClientEnd scSendToClientEnd = new SCSendToClientEnd();
        scSendToClientEnd.setToken(token);
        sendMessage(scSendToClientEnd);
    }

    private void syncPlayerPointToClientService() {
        if (clientPoint == null) {
            logger.error("PlayerObject {}: syncPlayerPointToClientService: clientPoint == null.", this);
            return;
        }
        // 修改ClientService的HumanToPoint连接点，并切换阶段
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        clientService.setPlayerPoint(playerId, playerPoint);
    }

    public String getToken() {
        return token;
    }

    public <T> void sendMessage(T jsonObject) {
        if (clientPoint == null) {
            logger.warn("PlayerObject {}: sendMessage: clientPoint == null.", this);
            return;
        }

        int protoID = ProtoScanner.getProtoID(jsonObject.getClass());
        IClientService proxy = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        proxy.sendMessage(Message.createMessage(protoID, jsonObject));
    }

    /**
     * HumanObject事件
     */
    public void fireEvent(IEvent event) {
        String eventKey = event.getClass().getSimpleName().toLowerCase();
        PlayerEventDispatcher.getInstance().dispatch(eventKey, method -> {
            Class<?> modClass = method.getDeclaringClass();
            return getModBase(modClass);
        }, event);
    }

    public String getAccount() {
        return playerDB != null ? playerDB.getAccount() : "";
    }

    public void init() {
        InitHMods();

        initHModServices();
    }

    private void InitHMods() {
        List<Class<? extends PlayerModBase>> hModClasses = PlayerModScanner.getPlayerModClasses();
        for (Class<? extends PlayerModBase> hModClass : hModClasses) {
            try {
                // 使用带参数的构造函数创建实例
                PlayerModBase playerModBase = hModClass.getConstructor(PlayerObject.class).newInstance(this);
                modBaseMap.put(hModClass, playerModBase);
            } catch (Exception e) {
                logger.error("HModBase init error", e);
            }
        }
        logger.info("{} 加载完成 {} 个HMod", this, hModClasses.size());
    }

    private void initHModServices() {
        List<Class<? extends PlayerServiceBase>> humanService = HumanRPCScanner.getHumanService();
        for (Class<? extends PlayerServiceBase> humanServiceClass : humanService) {
            try {
                PlayerServiceBase playerServiceBase = humanServiceClass.getConstructor(PlayerObject.class).newInstance(this);
                // 获取humanServiceClass实现的接口
                Class<?>[] interfaces = humanServiceClass.getInterfaces();
                for (Class<?> inter : interfaces) {
                    // 检查接口是否包含HumanRPCProxy注解
                    if (inter.isAnnotationPresent(HumanRPCProxy.class)) {
                        humanServiceBaseMap.put(inter.getSimpleName().toLowerCase(), playerServiceBase);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("HumanServiceBase init error", e);
            }
        }
    }

    public PlayerServiceBase getPlayerService(String classSimpleName) {
        return humanServiceBaseMap.get(classSimpleName);
    }

    public <T extends PlayerModBase> T getMod(Class<T> clazz) {
        return (T) modBaseMap.get(clazz);
    }

    public PlayerModBase getModBase(Class<?> clazz) {
        return modBaseMap.get(clazz);
    }

    public void pulse(long now) {
        timerQueue.update(now);

        modBaseMap.forEach((aClass, hModBase) -> hModBase.onPulse(now));
    }

    public void onPulseSec(long now) {
        if (state == PlayerStateEnum.READY) {
            if (disconnectTime != 0L) {
                // 5分钟自动下线
                if (now - disconnectTime > MAX_DROP_TIME) {
                    unload();
                }
            }
        }

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", playerId)
                .toString();
    }

    public void disconnect() {
        logger.info("断开连接");

        if (clientPoint != null) {
            IClientService proxy = ReferenceFactory.getProxy(IClientService.class, clientPoint);
            proxy.Disconnect();
            clientPoint = null;
        }

        disconnectTime = System.currentTimeMillis();
    }

    public void unload() {
        logger.info("unload");

        humanLeaveStage();

        // 改变状态: Unloading
        setState(PlayerStateEnum.UNLOADING);
    }

    private void humanLeaveStage() {
        IHumanService humanService = ReferenceFactory.getProxy(IHumanService.class, humanPoint);
        humanService.humanLeaveStage();
    }

    public void Destroy() {
        logger.info("Player {} Destroy", this);
    }



}
