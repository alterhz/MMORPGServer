package org.game.core.rpc;

import org.game.human.HumanObject;

public class HumanServiceBase {

    protected final HumanObject humanObj;

    public HumanServiceBase(HumanObject humanObj) {
        this.humanObj = humanObj;
    }

    public HumanObject getHumanObj() {
        return humanObj;
    }
}
