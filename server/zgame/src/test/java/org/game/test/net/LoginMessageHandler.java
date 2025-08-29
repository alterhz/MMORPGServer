package org.game.test.net;

import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.net.Message;
import org.game.core.message.ProtoListener;
import org.game.core.message.ProtoScanner;
import org.game.proto.login.CSQueryHumans;
import org.game.proto.login.CSSelectHuman;
import org.game.proto.login.SCLogin;
import org.game.proto.login.SCQueryHumans;

public class LoginMessageHandler {
    public static final Logger logger = LogManager.getLogger(LoginMessageHandler.class);

    private final Channel channel;

    public LoginMessageHandler(Channel channel)
    {
        this.channel = channel;
    }

    @ProtoListener(SCLogin.class)
    public void onLogin(Message message) {
        logger.info("登录成功");
        // 请求获取角色列表
        CSQueryHumans csQueryHumans = new CSQueryHumans();
        sendMessage(csQueryHumans);
    }

    @ProtoListener(SCQueryHumans.class)
    public void onQueryHumans(Message message) {
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
                sendMessage(csSelectHuman);
            }
        }
    }

    private <T> void sendMessage(T proto) {
        Integer protoID = ProtoScanner.getProtoID(proto.getClass());
        Message message = Message.createMessage(protoID, proto);
        channel.writeAndFlush(message.toBytes());
    }
}
