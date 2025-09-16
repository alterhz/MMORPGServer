package org.game;

public class BaseUtils {
    // 全局配置初始化
    public static void init(int serverId) {
        // 设置${sys:logPath}为../logs 目录,linux是/export/logs 目录
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) {
            System.setProperty("logPath", "/export/logs");
        } else {
            System.setProperty("logPath", "../logs");
        }

        // 设置 serverId 系统属性，默认值为 20001
        System.setProperty("serverId", String.valueOf(serverId));
    }
}
