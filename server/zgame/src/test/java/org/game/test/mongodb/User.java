package org.game.test.mongodb;

public class User {
    public String name;
    public int age;
    public String email;

    public User()
    {
    }

    public User(String name, int age, String email)
    {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String toString()
    {
        return "User[name=" + name + ", age=" + age + ", email=" + email + "]";
    }
}
