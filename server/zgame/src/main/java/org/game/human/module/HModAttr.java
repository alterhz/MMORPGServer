package org.game.human.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.HumanLoader;
import org.game.core.db.MongoDBAsyncClient;
import org.game.dao.HumanAttrDB;
import org.game.dao.HumanInfoDB;
import org.game.human.HModBase;
import org.game.human.HumanObject;

import java.util.ArrayList;
import java.util.List;

public class HModAttr extends HModBase {
    public static final Logger logger = LogManager.getLogger(HModAttr.class);

    private final List<HumanInfoDB> humanInfoDBs = new ArrayList<>();

    public HModAttr(HumanObject humanObj) {
        super(humanObj);
    }

    @HumanLoader(entity = HumanAttrDB.class)
    public void loadHumanAttrDB(List<HumanAttrDB> humanAttrDBs) {
        logger.info("加载HumanAttrDB：{}", humanAttrDBs);
        if (humanAttrDBs.size() < 2) {
            HumanAttrDB humanAttrDB = new HumanAttrDB();
            humanAttrDB.setId(null);
            humanAttrDB.setHumanId(humanObj.getId());

            ArrayList<HumanAttrDB.Attribute> attributes = new ArrayList<>();
            HumanAttrDB.Attribute attribute = new HumanAttrDB.Attribute();
            attribute.setKey(1);
            attribute.setValue(10);
            attributes.add(attribute);
            humanAttrDB.setAttributes(attributes);
            humanAttrDB.setDescription("ok");

            MongoDBAsyncClient.insertOne(humanAttrDB);

            logger.info("插入HumanAttrDB：{}", humanAttrDB);
        }
    }

    @Override
    protected void onInitAfterLoadDB() {
        super.onInitAfterLoadDB();

        HModInfo hMod = humanObj.getHMod(HModInfo.class);
        hMod.getInfo();

        logger.info("HModAttr init");
    }

    public String getAttr() {
        return "attr";
    }


}
