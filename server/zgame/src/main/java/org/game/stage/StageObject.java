package org.game.stage;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.stage.StageModScanner;
import org.game.stage.module.StageModBase;
import org.game.stage.unit.UnitObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景对象
 */
public class StageObject {

    public static final Logger logger = LogManager.getLogger(StageObject.class);

    private final int stageSn;
    private final long stageId;

    private final Map<Long, UnitObject> stageUnits = new HashMap<>();

    /**
     * 存储模块实例的映射
     */
    private final Map<Class<? extends StageModBase>, StageModBase> modules = new ConcurrentHashMap<>();

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

    public void pulse(long now) {
        // 复制stageUnits，然后心跳
        List<UnitObject> stageUnits = new ArrayList<>(this.stageUnits.values());

        for (UnitObject stageUnit : stageUnits) {
            stageUnit.onPulse(now);
        }

        modules.forEach((clazz, modBase) -> modBase.onPulse(now));
        onPulse(now);
    }

    public void pulseSec(long now) {
        // 复制stageUnits，然后心跳
        List<UnitObject> stageUnits = new ArrayList<>(this.stageUnits.values());

        for (UnitObject stageUnit : stageUnits) {
            stageUnit.onPulseSec(now);
        }

        modules.forEach((clazz, modBase) -> modBase.onPulseSec(now));
        onPulseSec(now);
    }



    public void enterStage(UnitObject unitObject) {
        if (stageUnits.containsKey(unitObject.getUnitId())) {
            logger.error("unitObject already exist. unitObject: {}", unitObject);
            return;
        }

        stageUnits.put(unitObject.getUnitId(), unitObject);

        modules.forEach((clazz, modBase) -> modBase.onUnitEnter(unitObject));

        unitObject.onEnterStage(this);
    }

    public void leaveStage(UnitObject unitObject) {
        if (!stageUnits.containsKey(unitObject.getUnitId())) {
            logger.error("unitObject not exist. unitObject: {}", unitObject);
            return;
        }

        stageUnits.remove(unitObject.getUnitId());

        modules.forEach((clazz, modBase) -> modBase.onUnitLeave(unitObject));

        unitObject.onLeaveStage(this);
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("stageSn", stageSn)
                .append("stageId", stageId)
                .toString();
    }



}