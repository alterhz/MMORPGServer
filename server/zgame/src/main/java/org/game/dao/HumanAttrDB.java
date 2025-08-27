package org.game.dao;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;
import org.game.core.db.Entity;

import java.util.List;

@Entity(collectionName = "humanAttrs")
public class HumanAttrDB {
    private ObjectId id;
    private String humanId;
    private List<Attribute> attributes;
    private String description;

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

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("humanId", humanId)
                .append("description", description)
                .toString();
    }

    public static class Attribute {
        private int key;
        private int value;

        public Attribute() {
        }

        public Attribute(int key, int value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
