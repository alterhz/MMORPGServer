package org.game.stage;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.event.IEvent;
import org.game.core.event.StageEventDispatcher;
import org.game.core.stage.StageModScanner;
import org.game.proto.scene.SCEnterStage;
import org.game.stage.event.EnterStageEvent;
import org.game.stage.event.LeaveStageEvent;
import org.game.stage.event.PulseEvent;
import org.game.stage.event.PulseSecEvent;
import org.game.stage.human.HumanObject;
import org.game.stage.module.StageModBase;
import org.game.stage.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景对象
 */
public class StageObject {

    public static final Logger logger = LogManager.getLogger(StageObject.class);

    private final int stageSn;
    private final long stageId;

    private final Map<Long, Entity> stageUnits = new HashMap<>();

    /**
     * 存储模块实例的映射
     */
    private final Map<Class<? extends StageModBase>, StageModBase> modules = new HashMap<>();

    public StageObject(int stageSn, long stageId) {
        this.stageSn = stageSn;
        this.stageId = stageId;

    }

    public void init() {
        logger.debug("StageObject {} 初始化", this);
        // 初始化场景模块
        initStageModules();
    }

    public void startup() {
        logger.debug("StageObject {} 启动", this);
    }

    public void destroy() {
        logger.debug("StageObject {} 销毁", this);
    }

    public int getStageSn() {
        return stageSn;
    }

    public long getStageId() {
        return stageId;
    }

    protected void onPulse(long now) {

    }

    protected void onPulseSec(long now) {

    }

    public void enterStage(Entity entity) {
        if (stageUnits.containsKey(entity.getEntityId())) {
            logger.error("unitObject already exist. unitObject: {}", entity);
            return;
        }

        stageUnits.put(entity.getEntityId(), entity);

        fireEvent(new EnterStageEvent(entity));

        entity.onEnterStage(this);

        if (entity instanceof HumanObject) {
            HumanObject humanObject = (HumanObject) entity;

            SCEnterStage enterStageResponse = new SCEnterStage();
            enterStageResponse.setStageSn(getStageSn());
            humanObject.sendMessage(enterStageResponse);
        }
    }

    public void leaveStage(Entity Entity) {
        if (!stageUnits.containsKey(Entity.getEntityId())) {
            logger.error("unitObject not exist. unitObject: {}", Entity);
            return;
        }

        stageUnits.remove(Entity.getEntityId());

        fireEvent(new LeaveStageEvent(Entity));

        Entity.onLeaveStage(this);
    }

    /**
     * 初始化场景模块
     */
    private void initStageModules() {
        List<Class<? extends StageModBase>> stageModClasses = StageModScanner.getStageModClasses();
        for (Class<? extends StageModBase> modClass : stageModClasses) {
            try {
                // 使用带参数的构造函数创建实例
                StageModBase stageModBase = modClass.getConstructor(StageObject.class).newInstance(this);
                modules.put(modClass, stageModBase);
            } catch (Exception e) {
                logger.error("StageModBase init error", e);
            }
        }
        logger.info("{} 加载完成 {} 个StageModBase", this, stageModClasses.size());
    }

    public void pulse(long now) {
        // 复制stageUnits，然后心跳
        List<Entity> stageUnits = new ArrayList<>(this.stageUnits.values());

        for (Entity stageUnit : stageUnits) {
            stageUnit.onPulse(now);
        }

        fireEvent(new PulseEvent());
        onPulse(now);
    }

    public void pulseSec(long now) {
        // 复制stageUnits，然后心跳
        List<Entity> stageUnits = new ArrayList<>(this.stageUnits.values());

        for (Entity stageUnit : stageUnits) {
            stageUnit.onPulseSec(now);
        }

        fireEvent(new PulseSecEvent());
        onPulseSec(now);
    }

    /**
     * 触发事件
     */
    public void fireEvent(IEvent event) {
        String eventKey = event.getClass().getSimpleName().toLowerCase();
        StageEventDispatcher.getInstance().dispatch(eventKey, method -> {
            Class<?> modClass = method.getDeclaringClass();
            return getModBase(modClass);
        }, event);
    }

    /**
     * 获取模块实例
     *
     * @param clazz 模块类
     * @param <T>   模块类型
     * @return 模块实例
     */
    public <T extends StageModBase> T getMod(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    public StageModBase getModBase(Class<?> clazz) {
        return modules.get(clazz);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("stageSn", stageSn)
                .append("stageId", stageId)
                .toString();
    }



}