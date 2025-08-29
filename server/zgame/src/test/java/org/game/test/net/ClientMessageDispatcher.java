package org.game.test.net;

import org.game.core.event.EventDispatcher;
import org.game.proto.MessageListener;
import org.game.proto.ProtoScanner;
import org.game.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class ClientMessageDispatcher extends EventDispatcher {
    public static final Logger logger = LoggerFactory.getLogger(ClientMessageDispatcher.class);

    public void init() {
        logger.info("ClientMessageDispatcher 初始化");

        // 扫描LoginService的所有方法，包含注解@MessageListener，注册到MessageDispatch
        Class<?> loginServiceClass = LoginMessageHandler.class;
        Method[] methods = loginServiceClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MessageListener.class)) {
                MessageListener annotation = method.getAnnotation(MessageListener.class);
                Class<?> clazz = annotation.value();
                Integer protoID = ProtoScanner.getProtoID(clazz);
                register(String.valueOf(protoID), method);
                logger.info("LoginMessageHandler 注册 {} {}", protoID, method.getName());
            }
        }
    }

}