package org.game.test.mongodb.sync;

import com.mongodb.client.MongoCollection;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.config.MyConfig;
import org.game.core.db.MongoDBSyncClient;
import org.game.dao.PlayerAttrDB;

public class HumanAttrDBSync {
    public static void main(String[] args) {
        BaseUtils.init(20000);

        MyConfig.load();

        MongoDBSyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        try {
            MongoCollection<PlayerAttrDB> humanAttrs = MongoDBSyncClient.getOrCreateCollection("humanAttrs", PlayerAttrDB.class);

            humanAttrs.drop();

            PlayerAttrDB playerAttrDB = new PlayerAttrDB();
            playerAttrDB.setId(null);
            playerAttrDB.setPlayerId(20001000000080L);
            playerAttrDB.setDescription("ok");


            humanAttrs.insertOne(playerAttrDB);

            humanAttrs.find().forEach(human -> {
                LogCore.logger.info("human={}", human);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
