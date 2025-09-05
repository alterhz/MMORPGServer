package org.game.human;

public class HModBase {

    protected final HumanObject humanObj;

    public HModBase(HumanObject humanObj) {
        this.humanObj = humanObj;
    }

    protected HumanObject getHumanObj() {
        return humanObj;
    }

    /**
     * DB加载完毕后的初始化
     */
    protected void onInitAfterLoadDB() {}

    /**
     * 所有的HMod#onInitAfterLoadDB都调用完毕，可以同步客户端数据了
     */
    protected void onSendToClient() {}

    protected void onPulse(long now) {

    }

    protected void onPulseSec(long now) {

    }
}