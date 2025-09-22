package org.game.proto.login;

public class Player {
    private long id;
    private String name;
    private String profession;

    public long getid() { return id; }
    public void setid(long value) { this.id = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getProfession() { return profession; }
    public void setProfession(String value) { this.profession = value; }
}
