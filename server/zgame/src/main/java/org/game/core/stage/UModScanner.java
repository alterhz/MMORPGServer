package org.game.core.stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.stage.entity.UnitModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UModScanner {

    public static final Logger logger = LogManager.getLogger(UModScanner.class);

    private static final List<Class<? extends UnitModBase>> stageUnitModClasses = new ArrayList<>();

    public static List<Class<? extends UnitModBase>> getStageUnitModClasses() {
        if (stageUnitModClasses.isEmpty()) {
            Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.stage");
            for (Class<?> clazz : classes) {
                if (UnitModBase.class.isAssignableFrom(clazz) && clazz != UnitModBase.class) {
                    logger.info("Found StageUnitModBase subclass: {}", clazz.getName());
                    stageUnitModClasses.add((Class<? extends UnitModBase>) clazz);
                }
            }
        }

        return stageUnitModClasses;
    }
}