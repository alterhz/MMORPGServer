package org.game.player.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.PlayerLoader;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.event.EventListener;
import org.game.core.message.ProtoListener;
import org.game.dao.PlayerInfoDB;
import org.game.player.PlayerModBase;
import org.game.player.PlayerObject;
import org.game.player.event.OnPlayerLoadComplete;
import org.game.player.event.OnSendToClient;
import org.game.proto.login.CSTest;
import org.game.proto.login.SCTest;

import java.util.List;

public class PModInfo extends PlayerModBase {

    public static final Logger logger = LogManager.getLogger(PModInfo.class);

    public PModInfo(PlayerObject playerObj) {
        super(playerObj);
    }

    public void getInfo() {
        logger.info("HModInfo getInfo");
    }

    @PlayerLoader(entity = PlayerInfoDB.class)
    public void loadPlayerInfo(List<PlayerInfoDB> playerInfoDBS) {
        logger.info("加载PlayerInfoDB：{}", playerInfoDBS);

        // 如果没有数据，则插入一条
        if (playerInfoDBS.size() < 2) {
            PlayerInfoDB playerInfoDB = new PlayerInfoDB();
            playerInfoDB.setId(null);
            playerInfoDB.setPlayerId(playerObj.getPlayerId());
            playerInfoDB.setInfo("这是测试数据");

            MongoDBAsyncClient.insertOne(playerInfoDB);

            logger.info("插入PlayerInfoDB：{}", playerInfoDB);
        }
    }

    @EventListener
    public void OnPlayerLoadComplete(OnPlayerLoadComplete onPlayerLoadComplete) {
        PModAttr hMod = playerObj.getMod(PModAttr.class);
        String attr = hMod.getAttr();
        logger.info("PModInfo init, attr: {}", attr);
    }

    @EventListener
    public void OnSendToClient(OnSendToClient onSendToClient) {
        logger.info("PModInfo OnSendToClient");
    }

    /**
     * 消息监听CS_TEST
     */
    @ProtoListener(CSTest.class)
    private void CSTest(CSTest csTest) {
        logger.info("接收到消息CS_TEST: {}", csTest);

        SCTest scTest = new SCTest();
        scTest.setMessage("这是测试消息");
        playerObj.sendMessage(scTest);
    }

}
