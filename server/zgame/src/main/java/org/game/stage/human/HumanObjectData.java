package org.game.stage.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.rpc.ToPoint;

public class HumanObjectData {

    private long playerId;

    private ToPoint clientPoint;

    public HumanObjectData() {
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    public void setClientPoint(ToPoint clientPoint) {
        this.clientPoint = clientPoint;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("humanId", playerId)
                .toString();
    }
}
