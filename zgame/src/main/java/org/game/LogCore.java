package org.game;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCore {
    public static final Logger logger = LoggerFactory.getLogger(LogCore.class);

    public static void test() {
        logger.info("test");
        logger.error("error");
    }

}
