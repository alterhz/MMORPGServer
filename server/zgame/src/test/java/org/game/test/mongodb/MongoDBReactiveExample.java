package org.game.test.mongodb;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.game.BaseUtils;
import org.reactivestreams.Publisher;

import java.util.concurrent.CountDownLatch;

public class MongoDBReactiveExample {

    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);

        // 用于阻塞主线程，等待异步操作完成
        CountDownLatch latch = new CountDownLatch(1);

        // 创建异步客户端
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        try {
            MongoDatabase database = client.getDatabase("testdb");
            MongoCollection<Document> collection = database.getCollection("users");



            // 插入文档
            Document user = new Document("name", "Bob")
                    .append("age", 25)
                    .append("city", "Shanghai");

            collection.insertOne(user)
                    .subscribe(new PrintSubscriber<>("插入成功: "));

            // 查询所有文档
            collection.find()
                    .subscribe(new PrintSubscriber<>("查询结果: "));

            Publisher<Document> first = collection.find().first();


            // 查询特定用户
            collection.find(Filters.eq("name", "Bob"))
                    .first()
                    .subscribe(new PrintSubscriber<>("找到用户: "));

            // 更新文档
            collection.updateOne(
                            Filters.eq("name", "Bob"),
                            Updates.set("age", 26)
                    )
                    .subscribe(new PrintSubscriber<>("更新结果: "));

            // 删除文档
//            collection.deleteOne(Filters.eq("name", "Bob"))
//                    .subscribe(new PrintSubscriber<>("删除结果: "));

        } finally {
            // 延迟关闭客户端（等待操作完成）
            Thread.sleep(2000);
            client.close();
            latch.countDown();
        }

        latch.await(); // 等待完成
    }

    // 自定义 Subscriber 打印结果
    static class PrintSubscriber<T> implements org.reactivestreams.Subscriber<T> {
        private final String prefix;
        private org.reactivestreams.Subscription subscription;

        public PrintSubscriber(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void onSubscribe(org.reactivestreams.Subscription s) {
            this.subscription = s;
            s.request(1); // 请求一个数据
        }

        @Override
        public void onNext(T t) {
            System.out.println(prefix + t);
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("❌ 异常: " + t.getMessage());
            t.printStackTrace();
        }

        @Override
        public void onComplete() {
            System.out.println("✅ 操作完成");
        }
    }
}
