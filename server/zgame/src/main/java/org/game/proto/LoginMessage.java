package org.game.proto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.core.net.Message;
import org.game.core.rpc.ToPoint;

public class LoginMessage {

    private final Message message;

    private final ToPoint clientPoint;

    public LoginMessage(Message message, ToPoint clientPoint) {
        this.message = message;
        this.clientPoint = clientPoint;
    }

    public Message getMessage() {
        return message;
    }

    public ToPoint getClientPoint() {
        return clientPoint;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("message", message)
                .append("clientPoint", clientPoint)
                .toString();
    }
}
