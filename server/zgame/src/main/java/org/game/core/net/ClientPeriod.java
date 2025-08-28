package org.game.core.net;

public enum ClientPeriod {
    LOGIN(0),
    SELECT_HUMAN(1),
    PLAYING(2),
    DISCONNECT(3);

    private final int value;

    private ClientPeriod(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ClientPeriod getPeriod(int period) {
        for (ClientPeriod p : values()) {
            if (p.value == period) {
                return p;
            }
        }
        return null;
    }
}
