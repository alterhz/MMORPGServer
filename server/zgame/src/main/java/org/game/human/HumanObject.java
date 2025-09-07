package org.game.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.TimerQueue;
import org.game.core.event.HumanEventDispatcher;
import org.game.core.event.IEvent;
import org.game.core.human.HModScanner;
import org.game.core.net.Message;
import org.game.core.rpc.*;
import org.game.dao.HumanDB;
import org.game.core.message.ProtoScanner;
import org.game.global.rpc.IClientService;
import org.game.human.event.OnHumanLoadComplete;
import org.game.human.event.OnSendToClient;
import org.game.proto.login.SCSendToClientBegin;
import org.game.proto.login.SCSendToClientEnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色对象
 */
public class HumanObject {

    public static final Logger logger = LogManager.getLogger(HumanObject.class);

    private final String id;

    private final ToPoint clientPoint;

    private final ToPoint humanObjPoint;

    private final HumanDB humanDB;

    private final List<String> loadingHModDBs = new ArrayList<>();

    /**
     * hModBase
     */
    private final Map<Class<?>, HModBase> hModBaseMap = new HashMap<>();

    /**
     * HumanServiceBase
     * key: classSimpleName, value: HumanServiceBase
     */
    private final Map<String, HumanServiceBase> humanServiceBaseMap = new HashMap<>();

    private final TimerQueue timerQueue = new TimerQueue();

    public HumanObject(HumanDB humanDB, ToPoint clientPoint, ToPoint humanObjPoint) {
        this.id = humanDB.getId().toHexString();
        this.humanDB = humanDB;
        this.clientPoint = clientPoint;
        this.humanObjPoint = humanObjPoint;
    }

    public String getId() {
        return id;
    }

    public HumanDB getHumanDB() {
        return humanDB;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    public ToPoint getHumanObjPoint() {
        return humanObjPoint;
    }

    public void addLoadingHModDB(String hModDB) {
        loadingHModDBs.add(hModDB);
        logger.debug("正在加载 {}", hModDB);
    }

    public void removeLoadingHModDB(String hModDB) {
        loadingHModDBs.remove(hModDB);
        logger.debug("加载完成 {}", hModDB);
    }

    public void loadHModComplete() {
        if (loadingHModDBs.isEmpty()) {
            onLoadingComplete();
        }
    }

    /**
     * 所有HMod加载完成
     */
    protected void onLoadingComplete() {
        fireEvent(new OnHumanLoadComplete());

        // 修改ClientService的HumanToPoint连接点，并切换阶段
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        clientService.setHumanToPoint(id, humanObjPoint);

        // 发送协议
        SCSendToClientBegin scSendToClientBegin = new SCSendToClientBegin();
        sendMessage(scSendToClientBegin);

        fireEvent(new OnSendToClient());

        SCSendToClientEnd scSendToClientEnd = new SCSendToClientEnd();
        sendMessage(scSendToClientEnd);
    }

    public <T> void sendMessage(T jsonObject) {
        Integer protoID = ProtoScanner.getProtoID(jsonObject.getClass());
        IClientService proxy = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        proxy.sendMessage(Message.createMessage(protoID, jsonObject));
    }

    /**
     * HumanObject事件
     */
    public void fireEvent(IEvent event) {
        String eventKey = event.getClass().getSimpleName().toLowerCase();
        HumanEventDispatcher.getInstance().dispatch(eventKey, method -> {
            Class<?> hModClass = method.getDeclaringClass();
            return getHModBase(hModClass);
        }, event);
    }

    public String getAccount() {
        return humanDB != null ? humanDB.getAccount() : "";
    }

    public void init() {
        InitHMods();

        initHModServices();
    }

    private void InitHMods() {
        List<Class<? extends HModBase>> hModClasses = HModScanner.getHModClasses();
        for (Class<? extends HModBase> hModClass : hModClasses) {
            try {
                // 使用带参数的构造函数创建实例
                HModBase hModBase = hModClass.getConstructor(HumanObject.class).newInstance(this);
                hModBaseMap.put(hModClass, hModBase);
            } catch (Exception e) {
                logger.error("HModBase init error", e);
            }
        }
        logger.info("{} 加载完成 {} 个HMod", this, hModClasses.size());
    }

    private void initHModServices() {
        List<Class<? extends HumanServiceBase>> humanService = HumanRPCScanner.getHumanService();
        for (Class<? extends HumanServiceBase> humanServiceClass : humanService) {
            try {
                HumanServiceBase humanServiceBase = humanServiceClass.getConstructor(HumanObject.class).newInstance(this);
                // 获取humanServiceClass实现的接口
                Class<?>[] interfaces = humanServiceClass.getInterfaces();
                for (Class<?> inter : interfaces) {
                    // 检查接口是否包含HumanRPCProxy注解
                    if (inter.isAnnotationPresent(HumanRPCProxy.class)) {
                        humanServiceBaseMap.put(inter.getSimpleName().toLowerCase(), humanServiceBase);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("HumanServiceBase init error", e);
            }
        }
    }

    public HumanServiceBase getHumanService(String classSimpleName) {
        return humanServiceBaseMap.get(classSimpleName);
    }

    public <T extends HModBase> T getHMod(Class<T> clazz) {
        return (T) hModBaseMap.get(clazz);
    }

    public HModBase getHModBase(Class<?> clazz) {
        return hModBaseMap.get(clazz);
    }

    public void pulse(long now) {
        timerQueue.update(now);

        hModBaseMap.forEach((aClass, hModBase) -> hModBase.onPulse(now));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .toString();
    }

    public void disconnect() {
        logger.info("断开连接");
        // TODO 保存数据
        saveHumanData();

        IClientService proxy = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        proxy.Disconnect();
    }

    private void saveHumanData() {
        logger.info("保存数据");
    }
}
