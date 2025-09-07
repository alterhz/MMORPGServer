package org.game.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class EventDispatcher {
    public static final Logger logger = LogManager.getLogger(EventDispatcher.class);

    public final Map<String, List<Method>> eventMap = new java.util.HashMap<>();

    /**
     * 初始化
     */
    public abstract void init();

    /**
     * 注册事件
     * @param eventName 事件名称
     * @param method  方法
     */
    public void register(String eventName, Method method) {
        method.setAccessible(true);
        Class<?> aClass = method.getDeclaringClass();
        logger.info("EventDispatcher 注册 {} {}", aClass, method.getName());
        eventMap.computeIfAbsent(eventName, k -> new java.util.ArrayList<>()).add(method);
    }

    /**
     * 触发事件
     * @param eventName 事件名称
     * @param obj 事件处理对象
     * @param args 参数
     */
    public void dispatch(String eventName, Object obj, Object... args) {
        List<Method> methods = eventMap.get(eventName);
        if (methods != null) {
            for (Method method : methods) {
                try {
                    method.invoke(obj, args);
                } catch (Exception e) {
                    logger.error("事件触发时，Method调用异常, eventName={}, method={}", eventName, method, e);
                }
            }
        } else {
            logger.error("事件触发时，Event未注册. eventName={}", eventName);
        }
    }

    /**
     * 触发事件
     * @param eventName 事件名称
     * @param function 返回obj
     * @param args 参数
     */
    public void dispatch(String eventName, Function<Method, Object> function, Object... args) {
        List<Method> methods = eventMap.get(eventName);
        if (methods != null) {
            for (Method method : methods) {
                try {
                    Object obj = function.apply(method);
                    method.invoke(obj, args);
                } catch (Exception e) {
                    logger.error("事件触发时，Method调用异常, eventName={}, method={}", eventName, method, e);
                }
            }
        } else {
            logger.error("事件触发时，Event未注册2. eventName={}", eventName);
        }
    }
}
