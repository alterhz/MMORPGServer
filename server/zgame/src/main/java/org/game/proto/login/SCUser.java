package org.game.proto.login;

public class SCUser {
    private String name;
    private long age;
    private String email;

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public long getAge() { return age; }
    public void setAge(long value) { this.age = value; }

    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
}
