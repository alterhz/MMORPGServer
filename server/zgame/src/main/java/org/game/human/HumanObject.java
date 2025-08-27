package org.game.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.TimerQueue;
import org.game.core.human.HModInitializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色对象
 */
public class HumanObject {

    public static final Logger logger = LogManager.getLogger(HumanObject.class);

    private final String id;

    private final Map<Class<?>, HModBase> hModBaseMap = new HashMap<>();

    private final TimerQueue timerQueue = new TimerQueue();

    public HumanObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void initHMods() {
        List<Class<? extends HModBase>> hModClasses = HModInitializer.getHModClasses();
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
}
