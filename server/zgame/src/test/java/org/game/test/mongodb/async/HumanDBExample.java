package org.game.test.mongodb.async;

import com.mongodb.reactivestreams.client.MongoCollection;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.config.MyConfig;
import org.game.core.db.MongoDBAsyncClient;
import org.game.dao.HumanDB;

import java.util.concurrent.CountDownLatch;

public class HumanDBExample {

    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);

        // 用于阻塞主线程，等待异步操作完成
        CountDownLatch latch = new CountDownLatch(1);

        MyConfig.load();

        MongoDBAsyncClient.init(MyConfig.getConfig().getMongodb().getUri(), MyConfig.getConfig().getMongodb().getDbName());

        MongoCollection<HumanDB> humans = MongoDBAsyncClient.getCollection("humans", HumanDB.class);

        try {
            // 创建测试对象
            HumanDB humanDB = new HumanDB();
            humanDB.setId(null); // 插入时通常为 null，由数据库生成
            humanDB.setAccount("testuser001");
            humanDB.setPassword("encodedPassword123"); // 实际应使用 BCrypt 加密
            humanDB.setName("张三");
            humanDB.setSex(true); // true 表示男性
            humanDB.setEmail("zhangsan@example.com");
            humanDB.setPhoneNumber("13800138000");
            humanDB.setBirthDate(java.time.LocalDate.of(1990, 5, 20));
            humanDB.setAddress("北京市朝阳区");
            humanDB.setIdCardNumber("110101199005201234");
            humanDB.setRegisterTime(java.time.LocalDateTime.now());
            humanDB.setLastLoginTime(java.time.LocalDateTime.now());
            humanDB.setIsActive(true);
            humanDB.setAvatarUrl("https://example.com/avatar/zhangsan.jpg");
            humanDB.setNickname("三哥");

            // 插入一条数据（假设 insertOne 返回 Mono<HumanDB>）
//            humans.insertOne(humanDB)
//                    .subscribe(
//                            new PrintSubscriber<>("插入成功: ")
//                    );

            // 等待插入完成后再查询（响应式编程中需注意顺序）
            // 更好的方式：使用 flatMap 保证顺序
            humans.find()
                    .subscribe(
                            new PrintSubscriber<>("查询结果: ", 10)
                    );

        } finally {
            // 延迟关闭客户端（等待操作完成）
            Thread.sleep(2000);
            MongoDBAsyncClient.close();
            latch.countDown();
        }

        latch.await(); // 等待完成
    }

    // 自定义 Subscriber 打印结果
    static class PrintSubscriber<T> implements org.reactivestreams.Subscriber<T> {
        private final int maxRequest;
        private final String prefix;

        public PrintSubscriber(String prefix, int maxRequest) {
            this.maxRequest = maxRequest;
            this.prefix = prefix;
        }

        public PrintSubscriber(String prefix) {
            this.maxRequest = 1;
            this.prefix = prefix;
        }

        @Override
        public void onSubscribe(org.reactivestreams.Subscription s) {
            s.request(maxRequest);
        }

        @Override
        public void onNext(T t) {
            LogCore.logger.info("{} {}", prefix, t);
        }

        @Override
        public void onError(Throwable t) {
            LogCore.logger.error("❌ 异常: {}", t.getMessage());
        }

        @Override
        public void onComplete() {
            LogCore.logger.info("✅ 完成");
        }
    }
}
