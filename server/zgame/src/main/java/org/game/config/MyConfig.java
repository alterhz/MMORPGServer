package org.game.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyConfig {
    private static final Logger logger = LogManager.getLogger(MyConfig.class);

    private static ConfigRoot config; // 使用强类型对象保存配置

    public static void load() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);
            mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            
            // 优先从外部目录加载config.yaml
            InputStream input = null;
            Path externalConfigPath = Paths.get("config.yaml");
            if (Files.exists(externalConfigPath)) {
                logger.info("从外部目录加载配置文件: {}", externalConfigPath.toAbsolutePath());
                input = Files.newInputStream(externalConfigPath);
            } else {
                // 从resources目录加载config.yaml
                logger.info("从jar包内加载配置文件");
                input = MyConfig.class.getClassLoader().getResourceAsStream("config.yaml");
            }
            
            if (input == null) {
                logger.error("找不到config.yaml配置文件");
                throw new RuntimeException("找不到config.yaml配置文件");
            }

            // 解析YAML配置
            config = mapper.readValue(input, ConfigRoot.class);

            logger.info("配置文件加载成功");
        } catch (Exception e) {
            logger.error("配置文件加载失败", e);
            throw new RuntimeException("配置文件加载失败: " + e.getMessage());
        }
    }

    public static ConfigRoot getConfig() {
        return config;
    }

    // 获取服务器名称前缀


    // 使用嵌套类结构表示配置
    public static class ConfigRoot {
        private ServerConfig server;
        private GameThreadConfig game_thread;
        private HumanThreadConfig human_thread;
        private ConnThreadConfig conn_thread;
        private StageThreadConfig stage_thread;
        private MongoDbConfig mongodb;

        public ServerConfig getServer() {
            return server;
        }

        public void setServer(ServerConfig server) {
            this.server = server;
        }

        public GameThreadConfig getGameThread() {
            return game_thread;
        }

        public void setGameThread(GameThreadConfig game_thread) {
            this.game_thread = game_thread;
        }

        public HumanThreadConfig getHumanThread() {
            return human_thread;
        }

        public void setHumanThread(HumanThreadConfig human_thread) {
            this.human_thread = human_thread;
        }

        public ConnThreadConfig getConnThread() {
            return conn_thread;
        }

        public void setConnThread(ConnThreadConfig conn_thread) {
            this.conn_thread = conn_thread;
        }
        
        public StageThreadConfig getStageThread() {
            return stage_thread;
        }

        public void setStageThread(StageThreadConfig stage_thread) {
            this.stage_thread = stage_thread;
        }

        public MongoDbConfig getMongodb() {
            return mongodb;
        }

        public void setMongodb(MongoDbConfig mongodb) {
            this.mongodb = mongodb;
        }
    }

    public static class ServerConfig {
        private String prefix;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    public static class GameThreadConfig {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class HumanThreadConfig {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class ConnThreadConfig {
        private int count;
        // 每帧处理消息的数量
        private int frame_message_count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getFrameMessageCount() {
            return frame_message_count;
        }

        public void setFrameMessageCount(int frame_message_count) {
            this.frame_message_count = frame_message_count;
        }
    }
    
    public static class StageThreadConfig {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class MongoDbConfig {
        private String uri;
        private String db_name;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getDbName() {
            return db_name;
        }

        public void setDbName(String db_name) {
            this.db_name = db_name;
        }
    }
}