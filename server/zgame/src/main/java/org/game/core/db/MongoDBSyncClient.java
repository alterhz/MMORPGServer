package org.game.core.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;

/**
 * MongoDB 同步客户端工具类
 * 使用 mongodb-driver-sync 驱动
 */
public class MongoDBSyncClient {

    public static final Logger logger = LogManager.getLogger(MongoDBSyncClient.class);

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
            logger.info("MongoDB sync client initialized successfully. uri={}, dbName={}", uri, dbName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MongoDB sync client", e);
        }
    }

    /**
     * 使用默认数据库名获取集合
     */
    public static MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }

    /**
     * 使用默认数据库名获取集合
     */
    public static <T> MongoCollection<T> getCollection(String collectionName, Class<T> clazz) {
        return getDatabase().getCollection(collectionName, clazz);
    }

    /**
     * 获取默认数据库对象
     */
    public static MongoDatabase getDatabase() {
        if (defaultDbName == null) {
            throw new IllegalStateException("Default database name not set. Please call init(uri, dbName) method.");
        }
        return client.getDatabase(defaultDbName);
    }

    /**
     * 检查集合是否存在，如果不存在则创建
     *
     * @param collectionName  集合名
     * @return 返回集合对象
     */
    public static <T> MongoCollection<T> getOrCreateCollection(String collectionName, Class<T> clazz) {
        MongoDatabase database = getDatabase();

        // 列出所有集合名称
        List<String> collectionNames = database.listCollectionNames()
                .into(new java.util.ArrayList<>());

        if (!collectionNames.contains(collectionName)) {
            database.createCollection(collectionName);
            logger.info("集合 {} 创建成功。", collectionName);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("集合 {} 已经存在。", collectionName);
            }
        }

        return getCollection(collectionName, clazz);
    }

    public static MongoCollection<Document> getOrCreateCollection(String collectionName) {
        return getOrCreateCollection(collectionName, Document.class);
    }

    /**
     * 应用关闭时调用，释放资源
     */
    public static void close() {
        if (client != null) {
            client.close();
            client = null;
            logger.info("MongoDB sync client closed.");
        }
    }

    /**
     * 确保客户端已初始化（可用于健康检查）
     */
    public static boolean isInitialized() {
        return client != null;
    }
}