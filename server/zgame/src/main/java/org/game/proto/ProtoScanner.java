package org.game.proto;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProtoScanner {
    public static final Logger logger = LogManager.getLogger(ProtoScanner.class);
    /**
     * 协议ID与协议类的映射关系
     */
    public static final Map<Class<?>, Integer> PROTO_ID_MAP = new HashMap<>();

    // 类查找协议ID
    public static Integer getProtoID(Class<?> clazz) {
        return PROTO_ID_MAP.get(clazz);
    }

    /**
     * 扫描当前包下包含ProtoID注解的类，并获得注解的协议ID，并将类与协议ID映射起来
     */
    public static void init() {
        // 扫描org.game.proto包下的所有类
        Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.proto");
        for (Class<?> clazz : classes) {
            // 检查类是否包含ProtoID注解
            if (clazz.isAnnotationPresent(ProtoID.class)) {
                ProtoID protoIDAnnotation = clazz.getAnnotation(ProtoID.class);
                int protoId = protoIDAnnotation.value();
                PROTO_ID_MAP.put(clazz, protoId);
                logger.info("通过注解建立协议映射: {} <-> {}", clazz.getSimpleName(), protoId);
            }
        }
        logger.info("通过注解扫描完成，共找到 {} 个协议类", PROTO_ID_MAP.size());
    }
}