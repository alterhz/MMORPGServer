package org.game.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HModInfo extends HModBase{

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
}
