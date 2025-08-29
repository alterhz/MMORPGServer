package org.game.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.TimerQueue;
import org.game.core.human.HModScanner;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.dao.HumanDB;
import org.game.core.message.ProtoScanner;
import org.game.rpc.IClientService;

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

    private ToPoint clientPoint;

    private HumanDB humanDB;

    private final List<String> loadingHModDBs = new ArrayList<>();

    private final Map<Class<?>, HModBase> hModBaseMap = new HashMap<>();

    private final TimerQueue timerQueue = new TimerQueue();

    public HumanObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public HumanDB getHumanDB() {
        return humanDB;
    }

    public void setHumanDB(HumanDB humanDB) {
        this.humanDB = humanDB;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    public void setClientPoint(ToPoint clientPoint) {
        this.clientPoint = clientPoint;
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

    protected void onLoadingComplete() {
        logger.info("加载完成");
    }

    public <T> void sendMessage(T jsonObject) {
        Integer protoID = ProtoScanner.getProtoID(jsonObject.getClass());
        IClientService proxy = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        proxy.sendMessage(Message.createMessage(protoID, jsonObject));
    }

    public String getAccount() {
        return humanDB != null ? humanDB.getAccount() : "";
    }

    public void init() {
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

        for (Class<? extends HModBase> hModClass : hModClasses) {
            HModBase hModBase = hModBaseMap.get(hModClass);
            hModBase.onInit();
        }
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
