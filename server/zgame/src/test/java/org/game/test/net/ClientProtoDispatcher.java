package org.game.test.net;

import io.netty.channel.Channel;
import org.game.core.event.EventDispatcher;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.core.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class ClientProtoDispatcher extends EventDispatcher {
    public static final Logger logger = LoggerFactory.getLogger(ClientProtoDispatcher.class);

    protected Channel channel;

    public void init() {
        logger.info("消息函数监听初始化");

        // 扫描LoginService的所有方法，包含注解@MessageListener，注册到MessageDispatch
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ProtoListener.class)) {
                ProtoListener annotation = method.getAnnotation(ProtoListener.class);
                Class<?> clazz = annotation.value();
                Integer protoID = ProtoScanner.getProtoID(clazz);
                register(String.valueOf(protoID), method);
                logger.info("LoginMessageHandler 注册 {} {}", protoID, method.getName());
            }
        }
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected <T> void sendMessage(T proto) {
        Integer protoID = ProtoScanner.getProtoID(proto.getClass());
        Message message = Message.createMessage(protoID, proto);
        channel.writeAndFlush(message.toBytes());
    }

}