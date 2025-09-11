package org.game.proto.login;

import org.game.core.message.Proto;
import org.game.proto.ProtoIds;

/**
 * 用户凭证类，用于Jackson序列化和反序列化
 * 包含用户名和密码字段
 */
@Proto(ProtoIds.CS_LOGIN)
public class CSLogin {
    
    private String account;
    
    private String password;
    
    /**
     * 默认构造函数，用于Jackson反序列化
     */
    public CSLogin() {
    }
    
    /**
     * 带参数的构造函数
     * 
     * @param account 用户名
     * @param password 密码
     */
    public CSLogin(String account, String password) {
        this.account = account;
        this.password = password;
    }
    
    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getAccount() {
        return account;
    }
    
    /**
     * 设置用户名
     * 
     * @param account 用户名
     */
    public void setAccount(String account) {
        this.account = account;
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
                "username='" + account + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}