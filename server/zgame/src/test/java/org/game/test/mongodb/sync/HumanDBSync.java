package org.game.test.mongodb.sync;

import com.mongodb.client.MongoCollection;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.config.MyConfig;
import org.game.core.db.MongoDBSyncClient;
import org.game.dao.PlayerDB;

public class HumanDBSync {
    public static void main(String[] args) {
        BaseUtils.init(20000);

        MyConfig.load();

        MongoDBSyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        try {
            MongoCollection<PlayerDB> humans = MongoDBSyncClient.getOrCreateCollection("humans", PlayerDB.class);

            humans.drop();

            PlayerDB playerDB = new PlayerDB();

            // 随机测试数据赋值humanDB
            playerDB.setId(null);
            playerDB.setAccount("admin");
            playerDB.setPassword("admin");
            playerDB.setName("adminstrator");
            playerDB.setSex(true);
            playerDB.setEmail("admin@example.com");
            playerDB.setPhoneNumber("1234567");
            playerDB.setBirthDate(java.time.LocalDate.of(1973, 1, 1));
            playerDB.setAddress("青岛市李沧区");
            playerDB.setIdCardNumber("110101199005201234");
            playerDB.setRegisterTime(java.time.LocalDateTime.now());
            playerDB.setLastLoginTime(java.time.LocalDateTime.now());
            playerDB.setIsActive(true);
            playerDB.setAvatarUrl("https://example.com/avatar/admin.jpg");
            playerDB.setNickname("权哥");

            humans.insertOne(playerDB);

            humans.find().forEach(human -> {
                LogCore.logger.info("human={}", human);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
