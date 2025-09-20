package org.game.stage.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameThread;
import org.game.core.event.HumanEventDispatcher;
import org.game.core.event.IEvent;
import org.game.core.message.ProtoScanner;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.core.stage.StageHumanModScanner;
import org.game.global.rpc.IClientService;
import org.game.proto.scene.StageReadyNotify;
import org.game.stage.StageObject;
import org.game.stage.human.event.OnStageReadyEvent;
import org.game.stage.human.module.HumanModBase;
import org.game.stage.unit.UnitObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HumanObject extends UnitObject {

    private static final Logger logger = LogManager.getLogger(HumanObject.class);

    private final String humanId;

    private ToPoint clientPoint;

    private final Map<Class<?>, HumanModBase> stageHumanModMap = new HashMap<>();

    public HumanObject(StageObject stageObj, long unitId, String humanId) {
        super(unitId, stageObj);
        this.humanId = humanId;
    }

    public String getHumanId() {
        return humanId;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    public void setClientPoint(ToPoint clientPoint) {
        this.clientPoint = clientPoint;
    }

    public void init() {
        InitMods();

        // 更新ClientService的stageHumanPoint
        IClientService clientService = ReferenceFactory.getProxy(IClientService.class, clientPoint);
        ToPoint stageHumanPoint = new ToPoint(GameProcess.getGameProcessName(), GameThread.getCurrentThreadName(), humanId);
        clientService.setStageHumanToPoint(stageHumanPoint);

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
        List<Class<? extends HumanModBase>> stageHumanModClasses = StageHumanModScanner.getStageHumanModClasses();
        for (Class<? extends HumanModBase> modClass : stageHumanModClasses) {
            try {
                // 使用带参数的构造函数创建实例
                HumanModBase hModBase = modClass.getConstructor(HumanObject.class).newInstance(this);
                stageHumanModMap.put(modClass, hModBase);
            } catch (Exception e) {
                logger.error("StageHumanModBase init error", e);
            }
        }
        logger.info("{} 加载完成 {} 个StageHumanModBase", this, stageHumanModClasses.size());
    }

    public <T extends HumanModBase> T getMod(Class<T> clazz) {
        return (T) stageHumanModMap.get(clazz);
    }

    public HumanModBase getModBase(Class<?> clazz) {
        return stageHumanModMap.get(clazz);
    }

    // TODO 添加协议监听

    // TODO 添加事件监听
}
