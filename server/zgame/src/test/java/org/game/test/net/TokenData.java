package org.game.test.net;

public class TokenData {

    private static long playerId;

    private static String token = "";

    public static long getPlayerId() {
        return playerId;
    }

    public static void setPlayerId(long playerId) {
        TokenData.playerId = playerId;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TokenData.token = token;
    }
}

