package org.game.proto.login;

import java.util.List;
import org.game.core.message.Proto;

@Proto(value = 1004)
public class SCQueryHuman {

    private long code;

    private String message;

    private List<Human> human;

    public long getCode() {
        return code;
    }

    public void setCode(long value) {
        this.code = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public List<Human> getHuman() {
        return human;
    }

    public void setHuman(List<Human> value) {
        this.human = value;
    }
}
