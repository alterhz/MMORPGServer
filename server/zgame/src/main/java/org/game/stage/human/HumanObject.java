package org.game.stage.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.event.HumanEventDispatcher;
import org.game.core.event.IEvent;
import org.game.core.message.ProtoScanner;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.core.stage.HumanModScanner;
import org.game.global.rpc.IClientService;
import org.game.global.rpc.IStageGlobalService;
import org.game.proto.scene.StageReadyNotify;
import org.game.stage.StageObject;
import org.game.stage.human.event.OnStageReadyEvent;
import org.game.stage.unit.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumanObject extends Entity {

    private static final Logger logger = LogManager.getLogger(HumanObject.class);

    private ToPoint clientPoint;

    private ToPoint humanPoint;

    private final Map<Class<?>, HumanModBase> humanModBaseMap = new HashMap<>();

    /**
     * 角色对象使用playerId替换unitId
     */
    public HumanObject(StageObject stageObj, long unitId) {
        super(unitId, stageObj);
    }

    public long getPlayerId() {
        return this.entityId;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    public void setClientPoint(ToPoint clientPoint) {
        this.clientPoint = clientPoint;
    }

    public void setHumanPoint(ToPoint humanPoint) {
        this.humanPoint = humanPoint;
    }

    public ToPoint getHumanPoint() {
        return humanPoint;
    }

    public void init() {
        InitMods();

        // 更新ClientService的stageHumanPoint
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        ToPoint stageHumanPoint = getHumanPoint();
        clientService.setHumanPoint(stageHumanPoint);

        StageReadyNotify stageReadyNotify = new StageReadyNotify();
        stageReadyNotify.setStageSn(stageObj.getStageSn());
        sendMessage(stageReadyNotify);

        fireEvent(new OnStageReadyEvent());
    }

    public <T> void sendMessage(T jsonObject) {
        int protoID = ProtoScanner.getProtoID(jsonObject.getClass());
        IClientService proxy = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        proxy.sendMessage(Message.createMessage(protoID, jsonObject));
    }

    /**
     * HumanObject事件
     */
    public void fireEvent(IEvent event) {
        String eventKey = event.getClass().getSimpleName().toLowerCase();
        HumanEventDispatcher.getInstance().dispatch(eventKey, method -> {
            Class<?> modClass = method.getDeclaringClass();
            return getModBase(modClass);
        }, event);
    }

    private void InitMods() {
        List<Class<? extends HumanModBase>> humanModClasses = HumanModScanner.getStageHumanModClasses();
        for (Class<? extends HumanModBase> modClass : humanModClasses) {
            try {
                // 使用带参数的构造函数创建实例
                HumanModBase hModBase = modClass.getConstructor(HumanObject.class).newInstance(this);
                humanModBaseMap.put(modClass, hModBase);
            } catch (Exception e) {
                logger.error("StageHumanModBase init error", e);
            }
        }
        logger.info("{} 加载完成 {} 个StageHumanModBase", this, humanModClasses.size());
    }

    public <T extends HumanModBase> T getMod(Class<T> clazz) {
        return (T) humanModBaseMap.get(clazz);
    }

    public HumanModBase getModBase(Class<?> clazz) {
        return humanModBaseMap.get(clazz);
    }

    @Override
    public void onEnterStage(StageObject stageObj) {
        super.onEnterStage(stageObj);

        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);
        stageGlobalService.humanEnter(stageObj.getStageId());
    }

    @Override
    public void onLeaveStage(StageObject stageObj) {
        super.onLeaveStage(stageObj);

        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);
        stageGlobalService.humanLeave(stageObj.getStageId());
    }

    @Override
    public void onPulse(long now) {
        super.onPulse(now);

        humanModBaseMap.forEach((aClass, hModBase) -> hModBase.onPulse(now));
    }

    @Override
    public void onPulseSec(long now) {
        super.onPulseSec(now);

        humanModBaseMap.forEach((aClass, hModBase) -> hModBase.onPulseSec(now));
    }
}
