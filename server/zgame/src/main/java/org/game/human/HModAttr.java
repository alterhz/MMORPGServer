package org.game.human;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HModAttr extends HModBase {
    public static final Logger logger = LogManager.getLogger(HModAttr.class);

    public HModAttr(HumanObject humanObj) {
        super(humanObj);
    }

    @Override
    protected void onInit() {
        super.onInit();

        HModInfo hMod = humanObj.getHMod(HModInfo.class);
        hMod.getInfo();

        logger.info("HModAttr init");
    }

    public String getAttr() {
        return "attr";
    }

}
