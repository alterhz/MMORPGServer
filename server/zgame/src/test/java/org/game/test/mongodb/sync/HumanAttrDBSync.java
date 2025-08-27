package org.game.test.mongodb.sync;

import com.mongodb.client.MongoCollection;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.config.MyConfig;
import org.game.core.db.MongoDBSyncClient;
import org.game.dao.HumanAttrDB;
import org.game.dao.HumanDB;

public class HumanAttrDBSync {
    public static void main(String[] args) {
        BaseUtils.init(20000);

        MyConfig.load();

        MongoDBSyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        try {
            MongoCollection<HumanAttrDB> humanAttrs = MongoDBSyncClient.getOrCreateCollection("humanAttrs", HumanAttrDB.class);

            humanAttrs.drop();

            HumanAttrDB humanAttrDB = new HumanAttrDB();
            humanAttrDB.setId(null);
            humanAttrDB.setHumanId("admin");
            humanAttrDB.setDescription("ok");


            humanAttrs.insertOne(humanAttrDB);

            humanAttrs.find().forEach(human -> {
                LogCore.logger.info("human={}", human);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
