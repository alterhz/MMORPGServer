package org.game.test;

import org.bson.types.ObjectId;
import org.game.BaseUtils;
import org.game.config.MyConfig;
import org.game.core.db.DaoScanner;
import org.game.core.db.HumanDBManager;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.rpc.ToPoint;
import org.game.dao.PlayerDB;
import org.game.player.PlayerObject;

public class HumanDBManagerTest {
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);

        DaoScanner.init();

        MyConfig.load();

        MongoDBAsyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        HumanDBManager.init();

        PlayerDB playerDB = new PlayerDB();
        playerDB.setId(new ObjectId("admin"));

        PlayerObject humanObj = new PlayerObject(playerDB, new ToPoint());
        humanObj.init();

        HumanDBManager.loadHumanModDB(humanObj);

        Thread.sleep(50000);
    }
}
