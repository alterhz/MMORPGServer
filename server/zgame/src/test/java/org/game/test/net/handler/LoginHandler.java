package org.game.test.net.handler;

import io.netty.util.internal.MathUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.message.ProtoListener;
import org.game.core.net.Message;
import org.game.proto.login.*;
import org.game.test.net.ClientProtoDispatcher;

public class LoginHandler extends ClientProtoDispatcher {
    public static final Logger logger = LogManager.getLogger(LoginHandler.class);

    public LoginHandler()
    {
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
                CSCreateHuman csCreateHuman = new CSCreateHuman();
                // 随机一个名称
                csCreateHuman.setName("测试" + RandomUtils.nextInt(1, 9999));
                csCreateHuman.setProfession("magic");
                sendMessage(csCreateHuman);
            } else {
                logger.info("选择角色");
                CSSelectHuman csSelectHuman = new CSSelectHuman();
                csSelectHuman.setHumanId(scQueryHumans.getHumanList().get(0).getId());
                sendMessage(csSelectHuman);
            }
        }
    }

    @ProtoListener(SCCreateHuman.class)
    public void onCreateHuman(Message message) {
        // 处理消息
        SCCreateHuman scCreateHuman = message.getJsonObject(SCCreateHuman.class);
        if (scCreateHuman.isSuccess()) {
            logger.info("创建角色成功");
        } else {
            logger.info("创建角色失败");
        }
    }

}
