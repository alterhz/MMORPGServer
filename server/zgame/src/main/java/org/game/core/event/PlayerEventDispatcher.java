package org.game.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.human.PlayerModScanner;
import org.game.player.PlayerModBase;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class PlayerEventDispatcher extends EventDispatcher {

    public static final Logger logger = LogManager.getLogger(PlayerEventDispatcher.class);

    public static final PlayerEventDispatcher instance = new PlayerEventDispatcher();
    public static PlayerEventDispatcher getInstance() {
        return instance;
    }

    @Override
    public void init() {
        // 扫描所有的HMod，获取包含@EventListener注解的方法，注册到HumanEventDispatcher
        List<Class<? extends PlayerModBase>> hModClasses = PlayerModScanner.getPlayerModClasses();
        for (Class<? extends PlayerModBase> hModClass : hModClasses) {
            Method[] methods = hModClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(EventListener.class)) {
                    // method只有一个参数，必须是IEvent的子类；否则报错
                    Parameter[] parameters = method.getParameters();
                    if (parameters.length != 1) {
                        logger.error("事件监听器方法 {}#{} 参数数量不正确，必须有且仅有一个参数", 
                            hModClass.getSimpleName(), method.getName());
                        continue;
                    }
                    
                    Class<?> paramType = parameters[0].getType();
                    if (!IEvent.class.isAssignableFrom(paramType)) {
                        logger.error("事件监听器方法 {}#{} 参数类型不正确，必须是IEvent的子类", 
                            hModClass.getSimpleName(), method.getName());
                        continue;
                    }

                    String eventName = paramType.getSimpleName().toLowerCase();
                    getInstance().register(eventName, method);
                    logger.info("注册 PlayerEvent 监听器: {}#{}", hModClass.getSimpleName(), method.getName());
                }
            }
        }
    }

}