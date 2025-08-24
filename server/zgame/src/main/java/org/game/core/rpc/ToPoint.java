package org.game.core.rpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ToPoint {
    private String gameProcessName;
    private String gameThreadName;
    private String gameServiceName;

    public ToPoint() {}

    @JsonCreator
    public ToPoint(@JsonProperty("gameProcessName") String gameProcessName,
                   @JsonProperty("gameThreadName") String gameThreadName,
                   @JsonProperty("gameServiceName") String gameServiceName) {
        this.gameProcessName = gameProcessName;
        this.gameThreadName = gameThreadName;
        this.gameServiceName = gameServiceName;
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

    public String getGameServiceName() {
        return gameServiceName;
    }

    public void setGameServiceName(String gameServiceName) {
        this.gameServiceName = gameServiceName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("gameProcessName", gameProcessName)
                .append("gameThreadName", gameThreadName)
                .append("gameServiceName", gameServiceName)
                .toString();
    }
}