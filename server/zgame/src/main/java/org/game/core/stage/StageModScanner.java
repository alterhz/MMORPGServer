package org.game.core.stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.stage.module.StageModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StageModScanner {

    public static final Logger logger = LogManager.getLogger(StageModScanner.class);

    private static final List<Class<? extends StageModBase>> stageModClasses = new ArrayList<>();

    public static List<Class<? extends StageModBase>> getStageModClasses() {
        if (stageModClasses.isEmpty()) {
            Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.stage");
            for (Class<?> clazz : classes) {
                if (StageModBase.class.isAssignableFrom(clazz) && clazz != StageModBase.class) {
                    logger.info("Found StageModBase subclass: {}", clazz.getName());
                    stageModClasses.add((Class<? extends StageModBase>) clazz);
                }
            }
        }

        return stageModClasses;
    }
}