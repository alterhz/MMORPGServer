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
     * 扫描当前类的所有static变量名称，并扫描当前包下的所有类，并建立协议类与协议ID的映射关系
     * 例如：CS_LOGIN对应对应的是 org.game.proto.login.CSLogin
     * 建立一个协议类与协议ID的映射关系
     */
    public static void init() {
        // 获取Proto类中定义的所有协议ID
        Field[] fields = Proto.class.getFields();
        Map<String, Integer> protoIdMap = new HashMap<>();
        
        // 收集所有协议ID
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                try {
                    String fieldName = field.getName();
                    // 去掉下换线
                    fieldName = fieldName.replace("_", "");
                    int protoId = field.getInt(null);
                    protoIdMap.put(fieldName, protoId);
//                    logger.info("找到协议ID定义: {} = {}", fieldName, protoId);
                } catch (IllegalAccessException e) {
                    logger.error("无法访问Proto类中的字段: {}", field.getName(), e);
                }
            }
        }
        
        // 扫描org.game.proto包下的所有类
        Set<Class<?>> classes = ScanClassUtils.scanAllClasses("org.game.proto");
        for (Class<?> clazz : classes) {
            if (clazz.getSimpleName().contains("CS") || clazz.getSimpleName().contains("SC")) {
                String className = clazz.getSimpleName();
                // 查找对应的协议ID（通过类名匹配协议ID名）
                String protoIdName = className.toUpperCase();
                if (protoIdMap.containsKey(protoIdName)) {
                    int protoId = protoIdMap.get(protoIdName);
                    PROTO_ID_MAP.put(clazz, protoId);
                    logger.info("建立协议映射: {} <-> {}", className, protoId);
                }
            }
        }

        // PROTO_ID_MAP与protoIdMap数量不一致，说明协议ID定义有误
        if (PROTO_ID_MAP.size() != protoIdMap.size()) {
            throw new RuntimeException("协议ID定义有误，数量不一致！PROTO_ID_MAP=" + PROTO_ID_MAP.size() + ", protoIdMap=" + protoIdMap.size());
        }
        
        logger.info("协议扫描完成，共找到 {} 个协议类", PROTO_ID_MAP.size());
    }
}