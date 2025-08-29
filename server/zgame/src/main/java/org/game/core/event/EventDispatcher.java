package org.game.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    public static final Logger logger = LogManager.getLogger(EventDispatcher.class);

    public final Map<String, List<Method>> eventMap = new java.util.HashMap<>();

    public void register(String eventName, Method method) {
        method.setAccessible(true);
        eventMap.computeIfAbsent(eventName, k -> new java.util.ArrayList<>()).add(method);
    }

    public void dispatch(String eventName, Object obj, Object... args) {
        logger.info("EventDispatch 触发");
        List<Method> methods = eventMap.get(eventName);
        if (methods != null) {
            for (Method method : methods) {
                try {
                    method.invoke(obj, args);
                } catch (Exception e) {
                    logger.error("EventDispatch 触发异常", e);
                }
            }
        } else {
            logger.error("EventDispatch 未注册. eventName={}", eventName);
        }
    }
}
