package org.game.core.rpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FromPoint {
    private String gameProcessName;
    private String gameThreadName;

    public FromPoint() {}

    @JsonCreator
    public FromPoint(@JsonProperty("gameProcessName") String gameProcessName,
                     @JsonProperty("gameThreadName") String gameThreadName) {
        this.gameProcessName = gameProcessName;
        this.gameThreadName = gameThreadName;
    }

    public String getGameProcessName() {
        return gameProcessName;
    }

    public void setGameProcessName(String gameProcessName) {
        this.gameProcessName = gameProcessName;
    }

    public String getGameThreadName() {
        return gameThreadName;
    }

    public void setGameThreadName(String gameThreadName) {
        this.gameThreadName = gameThreadName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("gameProcessName", gameProcessName)
                .append("gameThreadName", gameThreadName)
                .toString();
    }
}