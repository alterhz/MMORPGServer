package org.game.test.mongodb.sync;

import com.mongodb.client.MongoCollection;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.config.MyConfig;
import org.game.core.db.MongoDBSyncClient;
import org.game.dao.HumanDB;

public class HumanDBSync {
    public static void main(String[] args) {
        BaseUtils.init(20000);

        MyConfig.load();

        MongoDBSyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        try {
            MongoCollection<HumanDB> humans = MongoDBSyncClient.getOrCreateCollection("humans", HumanDB.class);

            humans.drop();

            HumanDB humanDB = new HumanDB();

            // 随机测试数据赋值humanDB
            humanDB.setId(null);
            humanDB.setAccount("admin");
            humanDB.setPassword("admin");
            humanDB.setName("adminstrator");
            humanDB.setSex(true);
            humanDB.setEmail("admin@example.com");
            humanDB.setPhoneNumber("1234567");
            humanDB.setBirthDate(java.time.LocalDate.of(1973, 1, 1));
            humanDB.setAddress("青岛市李沧区");
            humanDB.setIdCardNumber("110101199005201234");
            humanDB.setRegisterTime(java.time.LocalDateTime.now());
            humanDB.setLastLoginTime(java.time.LocalDateTime.now());
            humanDB.setIsActive(true);
            humanDB.setAvatarUrl("https://example.com/avatar/admin.jpg");
            humanDB.setNickname("权哥");

            humans.insertOne(humanDB);

            humans.find().forEach(human -> {
                LogCore.logger.info("human={}", human);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
