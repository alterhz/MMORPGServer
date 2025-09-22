package org.game.global.service;

import com.mongodb.client.MongoCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.MongoDBSyncClient;
import org.game.dao.ServerDB;
import org.game.global.rpc.IServerService;

/**
 * 服务器服务，用于处理服务器级别的全局操作，如ID分配等
 */
public class ServerService extends GameServiceBase implements IServerService {

    public static final Logger logger = LogManager.getLogger(ServerService.class);

    private ServerDB serverDB;

    public ServerService(String name) {
        super(name);
    }

    @Override
    public void init() {
        logger.info("ServerService 初始化");

        MongoCollection<ServerDB> serverDBCollection = MongoDBSyncClient.getOrCreateCollection(ServerDB.class);
        ServerDB serverDB = serverDBCollection.find().first();
        if (serverDB == null) {
            // 创建表
            ServerDB newServerDB = new ServerDB(0L);
            MongoDBSyncClient.insertOne(newServerDB);
            this.serverDB = newServerDB;
        } else {
            // 获取表信息
            logger.info("表信息: {}", serverDB);
            this.serverDB = serverDB;
        }
    }

    @Override
    public void startup() {
        logger.info("ServerService 启动");
    }

    @Override
    public void destroy() {
        logger.info("ServerService 销毁");
    }

    @Override
    public void updateId() {

    }

    @Override
    public void hotfix(Param param) {
        logger.info("ServerService 热修复: param={}", param);
    }

}