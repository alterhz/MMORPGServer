package org.game.core.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.player.PlayerModBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerModScanner
{
    public static final Logger logger = LogManager.getLogger(PlayerModScanner.class);

    private static final List<Class<? extends PlayerModBase>> hModClasses = new ArrayList<>();

    public static List<Class<? extends PlayerModBase>> getPlayerModClasses() {
        if (hModClasses.isEmpty()) {
            Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.player");
            for (Class<?> clazz : classes) {
                if (PlayerModBase.class.isAssignableFrom(clazz) && clazz != PlayerModBase.class) {
                    logger.info("Found HModBase subclass: {}", clazz.getName());
                    hModClasses.add((Class<? extends PlayerModBase>) clazz);
                }
            }
        }

        return hModClasses;
    }
}
