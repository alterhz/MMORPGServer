package org.game.global.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.config.MyConfig;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.db.MongoDBSyncClient;
import org.game.core.human.IdAllocator;
import org.game.dao.ServerDB;
import org.game.global.rpc.IServerService;

/**
 * 服务器服务，用于处理服务器级别的全局操作，如ID分配等
 */
public class ServerService extends GameServiceBase implements IServerService {

    public static final Logger logger = LogManager.getLogger(ServerService.class);

    // ID分配器
    public static IdAllocator idAllocator;

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

        // 初始化ID分配器，使用serverId=1，起始序号为0
        int serverId = MyConfig.getConfig().getServer().getServerId();
        // 在实际应用中，这些值应该从配置文件中读取
        assert serverDB != null;
        idAllocator = new IdAllocator(serverId, serverDB.getCurrentSequence());
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
        // 保存到数据库
        MongoDBAsyncClient.getCollection(ServerDB.class).updateOne(
                        Filters.eq("_id", serverDB.getId()),
                        Updates.set("currentSequence", idAllocator.getCurrentSequence()))
                .subscribe(new MongoDBAsyncClient.UpdateSubscriber());
        logger.info("保存ID分配器信息: {}", idAllocator.getCurrentSequence());
    }

    @Override
    public void hotfix(Param param) {
        logger.info("ServerService 热修复: param={}", param);
    }

}