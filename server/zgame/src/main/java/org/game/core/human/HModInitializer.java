package org.game.core.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.human.HModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HModInitializer
{
    public static final Logger logger = LogManager.getLogger(HModInitializer.class);

    private static final List<Class<? extends HModBase>> hModClasses = new ArrayList<>();

    public static void initHMods() {
        // 通过反射查找所有的HModBase子类
        Set<Class<?>> classes = ScanClassUtils.scanServiceClasses();
        for (Class<?> clazz : classes) {
            if (HModBase.class.isAssignableFrom(clazz) && clazz != HModBase.class) {
                logger.info("Found HModBase subclass: " + clazz.getName());
                hModClasses.add((Class<? extends HModBase>) clazz);
            }
        }
    }

    public static List<Class<? extends HModBase>> getHModClasses() {
        return hModClasses;
    }
}
