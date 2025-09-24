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
import org.game.core.utils.Vector3;
import org.game.global.rpc.IClientService;
import org.game.global.rpc.IStageGlobalService;
import org.game.proto.scene.*;
import org.game.stage.StageObject;
import org.game.stage.human.event.OnStageReadyEvent;
import org.game.stage.entity.Entity;
import org.game.stage.entity.UnitObject;
import org.game.stage.entity.module.UModMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumanObject extends UnitObject {

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

        SCStageReady stageReadyNotify = new SCStageReady();
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
    @Override
    public void fireEvent(IEvent event) {
        super.fireEvent(event);
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
        super.onEnterStage(stageObj); // Entity中的方法是抽象的，不能调用

        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);
        stageGlobalService.humanEnter(stageObj.getStageId());
    }

    @Override
    public void onLeaveStage(StageObject stageObj) {
        super.onLeaveStage(stageObj); // Entity中的方法是抽象的，不能调用

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

    /**
     * 发送单位出现广播
     * @param unit 出现的单位
     */
    public void sendUnitAppear(Entity unit) {
        SCUnitAppear broadcast = new SCUnitAppear();
        List<Unit> units = new ArrayList<>();
        Unit u = new Unit();
        u.setUnitId(unit.getEntityId());
        u.setName(unit.getClass().getSimpleName() + "_" + unit.getEntityId());

        if (unit.getPosition() != null) {
            Position pos = new Position();
            pos.setX(unit.getPosition().getX());
            pos.setY(unit.getPosition().getY());
            pos.setZ(unit.getPosition().getZ());
            u.setPosition(pos);
        }

        // 设置生命值信息（示例值）
        u.setCurrentHealth(1000);
        u.setMaxHealth(1000);

        units.add(u);
        broadcast.setUnits(units);

        // 发送广播到客户端
        sendMessage(broadcast);
    }

    /**
     * 发送单位消失广播
     * @param unit 消失的单位
     */
    public void sendUnitDisappear(Entity unit) {
        SCUnitDisappear broadcast = new SCUnitDisappear();
        List<Long> unitIds = new ArrayList<>();
        unitIds.add(unit.getEntityId());
        broadcast.setUnitIds(unitIds);

        // 发送广播到客户端
        sendMessage(broadcast);
    }

}