package org.game.core.player;

import org.game.core.event.EventDispatcher;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.player.PlayerModBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class PlayerProtoDispatcher extends EventDispatcher {
    public static final Logger logger = LoggerFactory.getLogger(PlayerProtoDispatcher.class);

    private static final PlayerProtoDispatcher instance = new PlayerProtoDispatcher();

    public static PlayerProtoDispatcher getInstance() {
        return instance;
    }

    @Override
    public void init() {
        // 扫描所有的HMod，获取包含@ProtoListener注解的方法，注册到HumanProtoDispatcher
        List<Class<? extends PlayerModBase>> hModClasses = PlayerModScanner.getPlayerModClasses();
        for (Class<? extends PlayerModBase> hModClass : hModClasses) {
            Method[] methods = hModClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ProtoListener.class)) {
                    ProtoListener protoListener = method.getAnnotation(ProtoListener.class);
                    Class<?> protoClass = protoListener.value();
                    int protoID = ProtoScanner.getProtoID(protoClass);
                    getInstance().register(String.valueOf(protoID), method);
                    logger.info("注册 PlayerProto 监听器: {}#{}", hModClass.getSimpleName(), method.getName());
                }
            }
        }
    }

}