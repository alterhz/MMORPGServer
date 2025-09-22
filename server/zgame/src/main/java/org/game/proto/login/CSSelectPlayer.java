package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1005)
public class CSSelectPlayer {

    private long playerId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long value) {
        this.playerId = value;
    }
}
