package org.game.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanLoader;
import org.game.dao.HumanAttrDB;

import java.util.List;

public class HumanAttrManager {

    public static final Logger logger = LogManager.getLogger(HumanAttrManager.class);

    @HumanLoader(entity = HumanAttrDB.class)
    public static void loadHumanAttrDB(List<HumanAttrDB> humanAttrDBs) {
        logger.info("加载HumanAttrDB：{}", humanAttrDBs);
    }

}
