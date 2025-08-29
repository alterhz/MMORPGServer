package org.game.test.net;

import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.net.Message;
import org.game.proto.CSSelectHuman;
import org.game.proto.Proto;
import org.game.proto.SCQueryHumans;

public class MessageHandler {
    public static final Logger logger = LogManager.getLogger(MessageHandler.class);

    private final Channel channel;

    public MessageHandler(Channel channel)
    {
        this.channel = channel;
    }

    public void handle(Channel channel, Message message)
    {
        logger.info("收到服务器消息 - 协议ID: {}, 内容: {}", message.getProtoID(), message.getJsonStr());

        switch (message.getProtoID())
        {
            case Proto.SC_LOGIN:
                logger.info("登录成功");
                // 请求获取角色列表
                Message queryHumans = Message.createMessage(Proto.CS_QUERY_HUMANS, "");
                sendMessage(queryHumans);
                break;
            case Proto.SC_QUERY_HUMANS:
                // 处理消息
                SCQueryHumans scQueryHumans = message.getJsonObject(SCQueryHumans.class);
                if (scQueryHumans.getCode() == 0) {
                    if (scQueryHumans.getHumanList().isEmpty()) {
                        // 创角
                        logger.info("创建角色");
                    } else {
                        logger.info("选择角色");
                        CSSelectHuman csSelectHuman = new CSSelectHuman();
                        csSelectHuman.setHumanId(scQueryHumans.getHumanList().get(0).getId());
                        Message selectHuman = Message.createMessage(Proto.CS_SELECT_HUMAN, csSelectHuman);
                        sendMessage(selectHuman);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void sendMessage(Message message) {
        channel.writeAndFlush(message.toBytes());
    }
}
