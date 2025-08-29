package org.game.core.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.service.LoginService;

import java.lang.reflect.Method;


public class AccountMessageDispatcher extends EventDispatcher {
    public static final Logger logger = LogManager.getLogger(AccountMessageDispatcher.class);

    public void init() {
        logger.info("MessageDispatch 初始化");
        
        // 扫描LoginService的所有方法，包含注解@MessageListener，注册到MessageDispatch
        Class<?> loginServiceClass = LoginService.class;
        Method[] methods = loginServiceClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ProtoListener.class)) {
                ProtoListener annotation = method.getAnnotation(ProtoListener.class);
                Class<?> clazz = annotation.value();
                Integer protoID = ProtoScanner.getProtoID(clazz);
                register(String.valueOf(protoID), method);
                logger.info("MessageDispatch 注册 {} {}", protoID, method.getName());
            }
        }
    }
}
