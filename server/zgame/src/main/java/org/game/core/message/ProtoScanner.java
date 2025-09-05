package org.game.core.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProtoScanner {
    public static final Logger logger = LogManager.getLogger(ProtoScanner.class);
    /**
     * 协议ID与协议类的映射关系
     */
    public static final Map<Class<?>, Integer> PROTO_ID_MAP = new HashMap<>();
    /**
     * 协议ID与协议类的反向映射关系
     */
    public static final Map<Integer, Class<?>> ID_PROTO_MAP = new HashMap<>();

    // 类查找协议ID
    public static int getProtoID(Class<?> clazz) {
        return PROTO_ID_MAP.get(clazz);
    }

    // 协议ID查找类
    public static Class<?> getProtoClass(int protoId) {
        return ID_PROTO_MAP.get(protoId);
    }

    /**
     * 扫描当前包下包含ProtoID注解的类，并获得注解的协议ID，并将类与协议ID映射起来
     */
    public static void init() {
        // 扫描org.game.proto包下的所有类
        Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.proto");
        for (Class<?> clazz : classes) {
            // 检查类是否包含ProtoID注解
            if (clazz.isAnnotationPresent(Proto.class)) {
                Proto protoAnnotation = clazz.getAnnotation(Proto.class);
                int protoId = protoAnnotation.value();
                PROTO_ID_MAP.put(clazz, protoId);
                ID_PROTO_MAP.put(protoId, clazz);
                logger.info("通过注解建立协议映射: {} <-> {}", clazz.getSimpleName(), protoId);
            }
        }
        logger.info("通过注解扫描完成，共找到 {} 个协议类", PROTO_ID_MAP.size());
    }
}