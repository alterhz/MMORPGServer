package org.game.service;

import com.mongodb.client.MongoCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.MongoDBSyncClient;
import org.game.rpc.IDBService;

import java.util.concurrent.CompletableFuture;

/**
 * 数据库服务实现类，负责处理数据库相关操作
 * 继承自GameServiceBase，具备游戏服务的基本功能
 */
public class DBService extends GameServiceBase implements IDBService {

    public static final Logger logger = LogManager.getLogger(DBService.class);

    public DBService(String name) {
        super(name);
    }

    @Override
    public void init() {
        // 初始化数据库连接等操作
        logger.info("DBService 初始化");

        // TODO 同步查询表:server_info,不存在，则创建
        MongoCollection<Document> createCollection = MongoDBSyncClient.getOrCreateCollection("server_info");
        Document first = createCollection.find().first();
        if (first == null) {
            // 创建表
            Document document = new Document();
            document.append("name", "server_info");
            createCollection.insertOne(document);
        } else {
            // 获取表信息
            logger.info("表信息: {}", first);
        }

    }

    @Override
    public void startup() {
        // 启动数据库服务
        logger.info("DBService 启动");
    }

    @Override
    public void pulse(long now) {
        // 数据库相关的心跳逻辑
    }

    @Override
    public void destroy() {
        // 销毁数据库连接等操作
        logger.info("DBService 销毁");
    }

    @Override
    public void hotfix(Param param) {
        logger.info("DBService 热修复: param={}", param);
    }

    @Override
    public CompletableFuture<Object> query(Param param) {

        return CompletableFuture.completedFuture(param);
    }
}