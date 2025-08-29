package org.game.core.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

// ✅ 正确做法：全局单例或依赖注入一个 MongoClient
public class MongoDBAsyncClient {
    public static final Logger logger = LogManager.getLogger(MongoDBAsyncClient.class);

    private static MongoClient client;
    private static String defaultDbName;

    public static void init(String uri, String dbName) {
        // 创建异步客户端
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                        PojoCodecProvider.builder()
                                .automatic(true)
                                .build()
                )
        );

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .build();

        try {
            client = MongoClients.create(clientSettings);
            defaultDbName = dbName;
            logger.info("MongoDB async client initialized successfully. uri={}, dbName={}", uri, dbName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MongoDB async client", e);
        }
    }

    /**
     * 使用默认数据库名获取集合
     */
    public static MongoCollection<Document> getCollection(String collection) {
        return getDatabase().getCollection(collection);
    }

    public static <T> MongoCollection<T> getCollection(Class<T> clazz) {
        String collectionName = DaoScanner.getCollectionName(clazz);
        return getDatabase().getCollection(collectionName, clazz);
    }

    /**
     * 使用默认数据库名获取集合
     */
    public static <T> MongoCollection<T> getCollection(String collection, Class<T> clazz) {
        return getDatabase().getCollection(collection, clazz);
    }

    /**
     * 获取默认数据库
     */
    public static MongoDatabase getDatabase() {
        if (defaultDbName == null) {
            throw new IllegalStateException("Default database name not set. Please call init(uri, dbName) method.");
        }
        return client.getDatabase(defaultDbName);
    }

    public static <T> void insertOne(T obj) {
        Entity annotation = obj.getClass().getAnnotation(Entity.class);
        String collectionName = annotation.collectionName();
        Class<T> clazz = (Class<T>) obj.getClass();
        getCollection(clazz).insertOne(obj).subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(InsertOneResult insertOneResult) {
                logger.info("插入成功。{}", insertOneResult);
            }

            @Override
            public void onError(Throwable t) {
                logger.error("插入失败", t);
            }

            @Override
            public void onComplete() {
                logger.info("插入成功");
            }
        });
    }

    // 应用关闭时调用
    public static void close() {
        client.close();
    }
}