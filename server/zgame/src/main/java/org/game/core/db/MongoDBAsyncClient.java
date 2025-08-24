package org.game.core.db;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

// ✅ 正确做法：全局单例或依赖注入一个 MongoClient
public class MongoDBAsyncClient {
    public static final Logger logger = LogManager.getLogger(MongoDBAsyncClient.class);

    private static MongoClient client;
    private static String defaultDbName;

    public static void init(String uri, String dbName) {
        try {
            client = MongoClients.create(uri);
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

    /**
     * 获取默认数据库
     */
    public static MongoDatabase getDatabase() {
        if (defaultDbName == null) {
            throw new IllegalStateException("Default database name not set. Please call init(uri, dbName) method.");
        }
        return client.getDatabase(defaultDbName);
    }

    // 应用关闭时调用
    public static void close() {
        client.close();
    }
}