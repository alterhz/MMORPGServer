package org.game.core.stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.stage.human.HumanModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StageHumanModScanner {

    public static final Logger logger = LogManager.getLogger(StageHumanModScanner.class);

    private static final List<Class<? extends HumanModBase>> stageHumanModClasses = new ArrayList<>();

    public static List<Class<? extends HumanModBase>> getStageHumanModClasses() {
        if (stageHumanModClasses.isEmpty()) {
            Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.stage");
            for (Class<?> clazz : classes) {
                if (HumanModBase.class.isAssignableFrom(clazz) && clazz != HumanModBase.class) {
                    logger.info("Found StageHumanModBase subclass: {}", clazz.getName());
                    stageHumanModClasses.add((Class<? extends HumanModBase>) clazz);
                }
            }
        }

        return stageHumanModClasses;
    }
}
