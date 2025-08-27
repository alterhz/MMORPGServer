package org.game.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanLoader;
import org.game.dao.HumanInfoDB;

import java.util.List;

public class HumanInfoManager {

    public static final Logger logger = LogManager.getLogger(HumanInfoManager.class);

    @HumanLoader(entity = HumanInfoDB.class)
    public static void loadHumanAttrDB(List<HumanInfoDB> humanInfoDBs) {
        logger.info("加载HumanAttrDB：{}", humanInfoDBs);
    }
}
