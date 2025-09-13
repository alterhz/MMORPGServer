package org.game.proto.login;

import org.game.core.message.Proto;

@Proto(value = 1001)
public class CSLogin {

    private String account;

    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String value) {
        this.account = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }
}
