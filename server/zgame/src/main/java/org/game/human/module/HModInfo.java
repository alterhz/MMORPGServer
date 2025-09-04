package org.game.human.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanLoader;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.message.ProtoListener;
import org.game.core.net.Message;
import org.game.dao.HumanInfoDB;
import org.game.human.HModBase;
import org.game.human.HumanObject;
import org.game.proto.login.CSTest;
import org.game.proto.login.SCTest;

import java.util.List;

public class HModInfo extends HModBase {

    public static final Logger logger = LogManager.getLogger(HModInfo.class);

    public HModInfo(HumanObject humanObj) {
        super(humanObj);
    }

    @Override
    protected void onInit() {
        super.onInit();

        HModAttr hMod = humanObj.getHMod(HModAttr.class);
        String attr = hMod.getAttr();
        logger.info("HModInfo init, attr: {}", attr);
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

    /**
     * 消息监听CS_TEST
     */
    @ProtoListener(CSTest.class)
    private void CSTest(Message message) {
        logger.info("接收到消息CS_TEST: {}", message);

        SCTest scTest = new SCTest();
        humanObj.sendMessage(scTest);
    }

}
