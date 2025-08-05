package org.game.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameProcess {
    private static String name;
    private static final List<GameThread> gameThreads = new ArrayList<>();

    private GameProcess() {
        throw new IllegalStateException("Utility class");
    }

    public static void SetGameProcessName(String name) {
        GameProcess.name = name;
    }

    public static String getGameProcessName() {
        return name;
    }

    // 添加GameThread
    public static void addGameThread(GameThread thread) {
        gameThreads.add(thread);
    }

    // 根据名称获取GameThread
    public static GameThread getGameThread(String name) {
        return gameThreads.stream()
            .filter(t -> t.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    // 获取所有GameThread列表
    public static List<GameThread> getAllGameThreads() {
        return Collections.unmodifiableList(gameThreads);
    }

    // 获取游戏线程数量
    public static int getThreadCount() {
        return gameThreads.size();
    }
}

