package org.game.test.mongodb.async;

import org.bson.Document;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.config.MyConfig;
import org.game.core.db.MongoDBAsyncClient;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MongoDBMTTest {


    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);

        MyConfig.load();

        MongoDBAsyncClient.init(MyConfig.getMongoDbUri(), MyConfig.getMongoDbName());

        MongoDBAsyncClient.getCollection("users").find().subscribe(new Subscriber<Document>() {

            @Override
            public void onSubscribe(Subscription s) {
                LogCore.logger.info("DBService 订阅");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Document document) {
                LogCore.logger.info("DBService 接收到数据: {}", document);

            }

            @Override
            public void onError(Throwable t) {
                LogCore.logger.error("DBService 错误: ", t);
            }

            @Override
            public void onComplete() {
                LogCore.logger.info("DBService 完成");
            }
        });

        Thread.sleep(2000);
    }
}
