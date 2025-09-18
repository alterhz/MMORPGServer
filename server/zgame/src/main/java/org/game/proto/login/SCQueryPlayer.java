package org.game.proto.login;

import java.util.List;
import org.game.core.message.Proto;

@Proto(value = 1004)
public class SCQueryPlayer {

    private long code;

    private String message;

    private List<Player> player;

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

    public List<Player> getPlayer() {
        return player;
    }

    public void setPlayer(List<Player> value) {
        this.player = value;
    }
}
