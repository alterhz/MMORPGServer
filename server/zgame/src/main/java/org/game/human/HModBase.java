package org.game.human;

public class HModBase {

    protected final HumanObject humanObj;

    public HModBase(HumanObject humanObj) {
        this.humanObj = humanObj;
    }

    protected HumanObject getHumanObj() {
        return humanObj;
    }

    protected void onInit() {}

    protected void onPulse(long now) {

    }

    protected void onPulseSec(long now) {

    }
}