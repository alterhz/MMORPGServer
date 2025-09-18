package org.game.dao;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;
import org.game.core.db.Entity;

@Entity(collectionName = "PlayerInfos")
public class PlayerInfoDB {
    private ObjectId id;
    private String playerId;
    private String info;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("playerId", playerId)
                .append("info", info)
                .toString();
    }
}
