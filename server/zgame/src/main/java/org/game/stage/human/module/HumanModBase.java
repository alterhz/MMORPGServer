package org.game.stage.human.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.stage.human.HumanObject;

/**
 * 场景角色模块基类
 */
public class HumanModBase {

    public static final Logger logger = LogManager.getLogger(HumanModBase.class);

    private final HumanObject humanObj;

    public HumanModBase(HumanObject humanObj)
    {
        this.humanObj = humanObj;
    }

    public HumanObject getHumanObj()
    {
        return humanObj;
    }

    public <T extends HumanModBase> T getMod(Class<T> clazz) {
        return humanObj.getMod(clazz);
    }
}
