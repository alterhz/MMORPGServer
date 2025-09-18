package org.game.stage.human.module;

import org.game.stage.human.HumanObject;

/**
 * 场景角色模块基类
 */
public class HumanModBase {

    private final HumanObject stageHumanObj;

    public HumanModBase(HumanObject stageHumanObj)
    {
        this.stageHumanObj = stageHumanObj;
    }

    public HumanObject getStageHumanObj()
    {
        return stageHumanObj;
    }

    public <T extends HumanModBase> T getMod(Class<T> clazz) {
        return stageHumanObj.getMod(clazz);
    }
}
