package org.game.human.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.HumanServiceBase;
import org.game.human.HumanObject;
import org.game.human.MyStruct;
import org.game.human.rpc.IHumanAttrService;

public class HumanAttrService extends HumanServiceBase implements IHumanAttrService {

    public static final Logger logger = LogManager.getLogger(HumanAttrService.class);

    public HumanAttrService(HumanObject humanObj) {
        super(humanObj);
    }

    @Override
    public void test(String value, MyStruct myStruct) {
        logger.info("test: {}, {}, humanObj: {}", value, myStruct, humanObj);
    }
}
