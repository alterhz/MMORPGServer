package org.game.proto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用户凭证类，用于Jackson序列化和反序列化
 * 包含用户名和密码字段
 */
public class UserCredentials {
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;
    
    /**
     * 默认构造函数，用于Jackson反序列化
     */
    public UserCredentials() {
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param username 用户名
     * @param password 密码
     */
    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 设置用户名
     * 
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * 获取密码
     * 
     * @return 密码
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * 设置密码
     * 
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "UserCredentials{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}