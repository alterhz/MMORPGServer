package org.game.human.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanLoader;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.event.EventListener;
import org.game.core.message.ProtoListener;
import org.game.dao.HumanInfoDB;
import org.game.human.HModBase;
import org.game.human.HumanObject;
import org.game.human.event.OnHumanLoadComplete;
import org.game.human.event.OnSendToClient;
import org.game.proto.login.CSTest;
import org.game.proto.login.SCTest;

import java.util.List;

public class HModInfo extends HModBase {

    public static final Logger logger = LogManager.getLogger(HModInfo.class);

    public HModInfo(HumanObject humanObj) {
        super(humanObj);
    }

    public void getInfo() {
        logger.info("HModInfo getInfo");
    }

    @HumanLoader(entity = HumanInfoDB.class)
    public void loadHumanInfo(List<HumanInfoDB> humanInfoDBs) {
        logger.info("加载HumanInfoDB：{}", humanInfoDBs);

        // 如果没有数据，则插入一条
        if (humanInfoDBs.size() < 2) {
            HumanInfoDB humanInfoDB = new HumanInfoDB();
            humanInfoDB.setId(null);
            humanInfoDB.setHumanId(humanObj.getId());
            humanInfoDB.setInfo("这是测试数据");

            MongoDBAsyncClient.insertOne(humanInfoDB);

            logger.info("插入HumanInfoDB：{}", humanInfoDB);
        }
    }

    @EventListener
    public void OnHumanLoadComplete(OnHumanLoadComplete onHumanLoadComplete) {
        HModAttr hMod = humanObj.getHMod(HModAttr.class);
        String attr = hMod.getAttr();
        logger.info("HModInfo init, attr: {}", attr);
    }

    @EventListener
    public void OnSendToClient(OnSendToClient onSendToClient) {
        logger.info("HModInfo OnSendToClient");
    }

    /**
     * 消息监听CS_TEST
     */
    @ProtoListener(CSTest.class)
    private void CSTest(CSTest csTest) {
        logger.info("接收到消息CS_TEST: {}", csTest);

        SCTest scTest = new SCTest();
        scTest.setMessage("这是测试消息");
        humanObj.sendMessage(scTest);
    }

}
