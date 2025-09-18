package org.game.proto.scene;

import org.game.core.message.Proto;

@Proto(value = 50000)
public class StageReadyNotify {

    private long stageSn;

    public long getStageSn() {
        return stageSn;
    }

    public void setStageSn(long value) {
        this.stageSn = value;
    }
}
