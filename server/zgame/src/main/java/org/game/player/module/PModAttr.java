package org.game.player.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.db.PlayerLoader;
import org.game.core.db.MongoDBAsyncClient;
import org.game.core.event.EventListener;
import org.game.dao.PlayerAttrDB;
import org.game.dao.PlayerInfoDB;
import org.game.player.PlayerModBase;
import org.game.player.PlayerObject;
import org.game.player.event.OnPlayerLoadComplete;

import java.util.ArrayList;
import java.util.List;

public class PModAttr extends PlayerModBase {
    public static final Logger logger = LogManager.getLogger(PModAttr.class);

    private final List<PlayerInfoDB> playerInfoDBS = new ArrayList<>();

    public PModAttr(PlayerObject humanObj) {
        super(humanObj);
    }

    @PlayerLoader(entity = PlayerAttrDB.class)
    public void loadPlayerAttrDB(List<PlayerAttrDB> playerAttrDBS) {
        logger.info("加载PlayerAttrDB：{}", playerAttrDBS);
        if (playerAttrDBS.size() < 2) {
            PlayerAttrDB playerAttrDB = new PlayerAttrDB();
            playerAttrDB.setId(null);
            playerAttrDB.setPlayerId(playerObj.getId());

            ArrayList<PlayerAttrDB.Attribute> attributes = new ArrayList<>();
            PlayerAttrDB.Attribute attribute = new PlayerAttrDB.Attribute();
            attribute.setKey(1);
            attribute.setValue(10);
            attributes.add(attribute);
            playerAttrDB.setAttributes(attributes);
            playerAttrDB.setDescription("ok");

            MongoDBAsyncClient.insertOne(playerAttrDB);

            logger.info("插入PlayerAttrDB：{}", playerAttrDB);
        }
    }

    @EventListener
    public void OnPlayerLoadComplete(OnPlayerLoadComplete onPlayerLoadComplete) {
        logger.info("HumanLogin event. {}", onPlayerLoadComplete);

        PModInfo hMod = playerObj.getMod(PModInfo.class);
        hMod.getInfo();

        logger.info("PModAttr init");
    }

    public String getAttr() {
        return "attr";
    }


}
