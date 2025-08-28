package org.game.proto;

public class HumanInfo {
    private String id;
    private String name;
    private String profession;

    public HumanInfo() {
    }

    public HumanInfo(String id, String name, String profession) {
        this.id = id;
        this.name = name;
        this.profession = profession;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}