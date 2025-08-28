package org.game.core.db;

import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.human.HModScanner;
import org.game.human.HModBase;
import org.game.human.HumanObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class HumanDBManager {

    public static final Logger logger = LogManager.getLogger(HumanDBManager.class);

    private static final List<HumanLoaderMethodInfo> humanLoaderMethodInfos = new ArrayList<>();

    public static void init() {
        humanLoaderMethodInfos.addAll(scanAndLoadHumanDB());
    }

    public static void loadHumanDB(HumanObject humanObj) {

        for (HumanLoaderMethodInfo humanLoaderMethodInfo : humanLoaderMethodInfos) {
            final Class<Object> fromUnknownClass;
            MongoDBAsyncClient.getCollection(humanLoaderMethodInfo.getCollectionName(), humanLoaderMethodInfo.getEntity())
            .find(Filters.eq("humanId", humanObj.getId()))
                    .subscribe(new QuerySubscriber<Object>(Long.MAX_VALUE) {
                        @Override
                        protected void onLoadDB(List<Object> dbCollections) {
                            try {
                                logger.info("加载HumanDB成功. {}", humanLoaderMethodInfo.getCollectionName());
                                HModBase hModBase = humanObj.getHModBase(humanLoaderMethodInfo.getHModClass());
                                humanLoaderMethodInfo.getMethod().invoke(hModBase, dbCollections);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }

    }

    /**
     * 扫描所有的类，并扫描所有包含@HumanLoader注解的static函数
     */
    public static List<HumanLoaderMethodInfo> scanAndLoadHumanDB() {
        List<HumanLoaderMethodInfo> humanLoaderMethodInfos = new ArrayList<>();
        List<Class<? extends HModBase>> hModClasses = HModScanner.getHModClasses();
        for (Class<?> hModClazz : hModClasses) {
            // 不是HModBase子类，跳过
            if (!HModBase.class.isAssignableFrom(hModClazz) || hModClazz == HModBase.class) {
                continue;
            }

            // 遍历类中的所有方法，查找包含@HumanLoader注解的静态方法
            for (Method method : hModClazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(HumanLoader.class) && !Modifier.isStatic(method.getModifiers())) {
                    // 获取HumanLoader注解的值
                    HumanLoaderMethodInfo humanLoaderMethodInfo = getHumanLoaderMethodInfo(hModClazz, method);
                    humanLoaderMethodInfos.add(humanLoaderMethodInfo);
                }
            }
        }
        return humanLoaderMethodInfos;
    }

    private static HumanLoaderMethodInfo getHumanLoaderMethodInfo(Class<?> clazz, Method method) {
        HumanLoader loader = method.getAnnotation(HumanLoader.class);
        Class<?> entity = loader.entity();
        // 判断entity类是否包含@Entity注解
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException("类 " + entity.getName() + " 不包含@Entity注解");
        }

        Entity entityAnnotation = entity.getAnnotation(Entity.class);
        String collectionName = entityAnnotation.collectionName();

        return new HumanLoaderMethodInfo(clazz, entity, collectionName, method);
    }

    public static class  HumanLoaderMethodInfo {
        private final Class<?> hModClass;
        private final Class<?> entity;
        private final String collectionName;
        private final Method method;

        public HumanLoaderMethodInfo(Class<?> hModClass, Class<?> entity, String collectionName, Method method) {
            this.hModClass = hModClass;
            this.entity = entity;
            this.collectionName = collectionName;
            this.method = method;
        }

        public Class<?> getHModClass() {
            return hModClass;
        }

        public Class<?> getEntity() {
            return entity;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public Method getMethod() {
            return method;
        }
    }
}
