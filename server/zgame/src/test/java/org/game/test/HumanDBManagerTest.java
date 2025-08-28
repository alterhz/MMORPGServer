package org.game.test;

import org.game.BaseUtils;
import org.game.config.MyConfig;
import org.game.core.db.HumanDBManager;
import org.game.core.db.MongoDBAsyncClient;
import org.game.human.HumanObject;

public class HumanDBManagerTest {
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);

        MyConfig.load();

        MongoDBAsyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        HumanDBManager.init();

        HumanObject humanObj = new HumanObject("admin");
        humanObj.init();

        HumanDBManager.loadHumanDB(humanObj);

        Thread.sleep(50000);
    }
}
