package org.game.test;

import org.bson.types.ObjectId;
import org.game.BaseUtils;
import org.game.config.MyConfig;
import org.game.core.db.DaoScanner;
import org.game.core.db.HumanDBManager;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.rpc.ToPoint;
import org.game.dao.HumanDB;
import org.game.human.HumanObject;

public class HumanDBManagerTest {
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);

        DaoScanner.init();

        MyConfig.load();

        MongoDBAsyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        HumanDBManager.init();

        HumanDB humanDB = new HumanDB();
        humanDB.setId(new ObjectId("admin"));

        HumanObject humanObj = new HumanObject(humanDB, new ToPoint(), new ToPoint());
        humanObj.init();

        HumanDBManager.loadHumanModDB(humanObj);

        Thread.sleep(50000);
    }
}
