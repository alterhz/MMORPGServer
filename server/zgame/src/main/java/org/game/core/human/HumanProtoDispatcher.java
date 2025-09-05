package org.game.core.human;

import org.game.core.event.EventDispatcher;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.human.HModBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class HumanProtoDispatcher extends EventDispatcher {
    public static final Logger logger = LoggerFactory.getLogger(HumanProtoDispatcher.class);

    private static final HumanProtoDispatcher instance = new HumanProtoDispatcher();

    public static HumanProtoDispatcher getInstance() {
        return instance;
    }

    public void init() {
        // 扫描所有的HMod，获取包含@ProtoListener注解的方法，注册到HumanProtoDispatcher
        List<Class<? extends HModBase>> hModClasses = HModScanner.getHModClasses();
        for (Class<? extends HModBase> hModClass : hModClasses) {
            Method[] methods = hModClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(ProtoListener.class)) {
                    ProtoListener protoListener = method.getAnnotation(ProtoListener.class);
                    Class<?> protoClass = protoListener.value();
                    int protoID = ProtoScanner.getProtoID(protoClass);
                    getInstance().register(String.valueOf(protoID), method);
                    logger.info("注册 HumanProto 监听器: {}#{}", hModClass.getSimpleName(), method.getName());
                }
            }
        }
    }

}