package org.game.dao;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;
import org.game.core.db.Entity;

@Entity(collectionName = "humanInfos")
public class HumanInfoDB {
    private ObjectId id;
    private String humanId;
    private String info;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getHumanId() {
        return humanId;
    }

    public void setHumanId(String humanId) {
        this.humanId = humanId;
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
                .append("humanId", humanId)
                .append("info", info)
                .toString();
    }
}
