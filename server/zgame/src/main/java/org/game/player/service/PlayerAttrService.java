package org.game.player.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.PlayerServiceBase;
import org.game.player.PlayerObject;
import org.game.player.module.MyStruct;
import org.game.player.rpc.IPlayerAttrService;

public class PlayerAttrService extends PlayerServiceBase implements IPlayerAttrService {

    public static final Logger logger = LogManager.getLogger(PlayerAttrService.class);

    public PlayerAttrService(PlayerObject humanObj) {
        super(humanObj);
    }

    @Override
    public void test(String value, MyStruct myStruct) {
        logger.info("test: {}, {}, humanObj: {}", value, myStruct, playerObj);
    }
}
