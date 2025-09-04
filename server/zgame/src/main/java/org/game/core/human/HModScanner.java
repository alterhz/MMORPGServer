package org.game.core.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.human.HModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HModScanner
{
    public static final Logger logger = LogManager.getLogger(HModScanner.class);

    private static final List<Class<? extends HModBase>> hModClasses = new ArrayList<>();

    public static List<Class<? extends HModBase>> getHModClasses() {
        if (hModClasses.isEmpty()) {
            Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.human");
            for (Class<?> clazz : classes) {
                if (HModBase.class.isAssignableFrom(clazz) && clazz != HModBase.class) {
                    logger.info("Found HModBase subclass: {}", clazz.getName());
                    hModClasses.add((Class<? extends HModBase>) clazz);
                }
            }
        }

        return hModClasses;
    }
}
