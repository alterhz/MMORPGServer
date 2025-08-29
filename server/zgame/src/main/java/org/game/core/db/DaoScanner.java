package org.game.core.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DaoScanner {

    public static final Logger logger = LogManager.getLogger(DaoScanner.class);

    /**
     * 缓存实体类, key:实体类, value:集合名
     * <p>线程安全的事实不可变对象</p>
     */
    private static final Map<Class<?>, String> entityMap = new HashMap<>();

    public static void init() {
        Set<Class<?>> classes = ScanClassUtils.scanAllClasses();
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Entity.class)) {
                Entity entity = clazz.getAnnotation(Entity.class);
                String collectionName = entity.collectionName();
                entityMap.put(clazz, collectionName);
            }
        }
    }

    public static String getCollectionName(Class<?> clazz) {
        return entityMap.get(clazz);
    }
}
