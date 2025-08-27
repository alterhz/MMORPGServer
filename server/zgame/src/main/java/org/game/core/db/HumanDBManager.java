package org.game.core.db;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.utils.ScanClassUtils;
import org.game.dao.HumanAttrDB;
import org.game.dao.HumanDB;
import org.game.human.HumanObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HumanDBManager {

    public static final Logger logger = LogManager.getLogger(HumanDBManager.class);

    public static void loadHumanDB(String humanId, HumanObject humanObj) {
        List<HumanLoaderMethodInfo> humanLoaderMethodInfos = scanAndLoadHumanDB();
        for (HumanLoaderMethodInfo humanLoaderMethodInfo : humanLoaderMethodInfos) {
            final Class<Object> fromUnknownClass;
//            try {
//                fromUnknownClass = createFromUnknownClass(humanLoaderMethodInfo.getEntity());
//            } catch (InstantiationException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
            MongoDBAsyncClient.getCollection(humanLoaderMethodInfo.getCollectionName(), humanLoaderMethodInfo.getEntity())
            .find(Filters.eq("humanId", humanId))
                    .first()
                    .subscribe(new QuerySubscriber<Object>() {
                        @Override
                        protected void onLoadDB(List<Object> dbCollections) {
                            try {
                                logger.info("加载HumanDB成功. {}", humanLoaderMethodInfo.getCollectionName());
                                humanLoaderMethodInfo.getMethod().invoke(humanObj, dbCollections);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }

    }

    public static <T> T createFromUnknownClass(Class<?> unknownClass)
            throws InstantiationException, IllegalAccessException {
        // ⚠️ 警告：unchecked cast
        // 你必须 100% 确定 unknownClass 实际上就是 T 的 Class 对象，否则运行时会抛 ClassCastException。
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) unknownClass;

        return clazz.newInstance();
    }

    /**
     * 扫描所有的类，并扫描所有包含@HumanLoader注解的static函数
     */
    public static List<HumanLoaderMethodInfo> scanAndLoadHumanDB() {
        List<HumanLoaderMethodInfo> humanLoaderMethodInfos = new ArrayList<>();
        Set<Class<?>> classes = ScanClassUtils.scanAllClasses();
        for (Class<?> clazz : classes) {
            // 遍历类中的所有方法，查找包含@HumanLoader注解的静态方法
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(HumanLoader.class) && Modifier.isStatic(method.getModifiers())) {
                    // 获取HumanLoader注解的值
                    HumanLoaderMethodInfo humanLoaderMethodInfo = getHumanLoaderMethodInfo(method);
                    humanLoaderMethodInfos.add(humanLoaderMethodInfo);
                }
            }
        }
        return humanLoaderMethodInfos;
    }

    private static HumanLoaderMethodInfo getHumanLoaderMethodInfo(Method method) {
        HumanLoader loader = method.getAnnotation(HumanLoader.class);
        Class<?> entity = loader.entity();
        // 判断entity类是否包含@Entity注解
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new RuntimeException("类 " + entity.getName() + " 不包含@Entity注解");
        }

        Entity entityAnnotation = entity.getAnnotation(Entity.class);
        String collectionName = entityAnnotation.collectionName();

        return new HumanLoaderMethodInfo(entity, collectionName, method);
    }

    public static class  HumanLoaderMethodInfo {
        private final Class<?> entity;
        private final String collectionName;
        private final Method method;

        public HumanLoaderMethodInfo(Class<?> entity, String collectionName, Method method) {
            this.entity = entity;
            this.collectionName = collectionName;
            this.method = method;
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
