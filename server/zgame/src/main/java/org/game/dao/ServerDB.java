package org.game.dao;

import org.bson.types.ObjectId;
import org.game.core.db.Entity;

@Entity(collectionName = "server")
public class ServerDB {
    private ObjectId id;
    private long currentSequence;

    public ServerDB() {
    }

    public ServerDB(long currentSequence) {
        this.currentSequence = currentSequence;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(long currentSequence) {
        this.currentSequence = currentSequence;
    }

    @Override
    public String toString() {
        return "ServerDB{" +
                "id=" + id +
                ", currentSequence=" + currentSequence +
                '}';
    }
}