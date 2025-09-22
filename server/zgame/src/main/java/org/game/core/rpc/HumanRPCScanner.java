package org.game.core.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HumanRPCScanner {
    public static final Logger logger = LogManager.getLogger(HumanRPCScanner.class);

    private static final List<Class<? extends PlayerServiceBase>> humanServices = new ArrayList<>();

    public static List<Class<? extends PlayerServiceBase>> getHumanService() {
        if (humanServices.isEmpty()) {
            Set<Class<?>> classes = ScanClassUtils.scanAllClasses();
            for (Class<?> clazz : classes) {
                // 继承 HumanServiceBase 的类
                if (PlayerServiceBase.class.isAssignableFrom(clazz) && clazz != PlayerServiceBase.class) {
                    logger.info("Found HumanServiceBase subclass: {}", clazz.getName());
                    humanServices.add((Class<? extends PlayerServiceBase>) clazz);
                }
            }
        }

        return humanServices;
    }
}
