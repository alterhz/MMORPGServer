package org.game.test.net.handler;

import org.apache.commons.lang3.RandomUtils;
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
        SCQueryHumans scQueryHumans = message.getProto(SCQueryHumans.class);
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
        SCCreateHuman scCreateHuman = message.getProto(SCCreateHuman.class);
        if (scCreateHuman.isSuccess()) {
            logger.info("创建角色成功");
        } else {
            logger.info("创建角色失败");
        }
    }

    // 选择角色成功
    @ProtoListener(SCSelectHuman.class)
    public void onSelectHuman(Message message) {
        // 处理消息
        SCSelectHuman scSelectHuman = message.getProto(SCSelectHuman.class);
        if (scSelectHuman.getCode() == 0) {
            logger.info("选择角色成功");
            // 测试
            CSTest csTest = new CSTest();
            sendMessage(csTest);
        } else {
            logger.info("选择角色失败");
        }
    }

    // SCSendToClientBegin
    @ProtoListener(SCSendToClientBegin.class)
    public void onSendToClientBegin(Message message) {
        // 处理消息
        SCSendToClientBegin scSendToClientBegin = message.getProto(SCSendToClientBegin.class);
        logger.info("开始发送数据");
    }

    // SCSendToClientEnd
    @ProtoListener(SCSendToClientEnd.class)
    public void onSendToClientEnd(Message message) {
        // 处理消息
        SCSendToClientEnd scSendToClientEnd = message.getProto(SCSendToClientEnd.class);
        logger.info("结束发送数据");

        // 测试
        CSTest csTest = new CSTest();
        sendMessage(csTest);
    }

    // 测试
    @ProtoListener(SCTest.class)
    public void onTest(Message message) {
        // 处理消息
        SCTest scTest = message.getProto(SCTest.class);
        logger.info("测试结果：{}", scTest.getMessage());
    }

}
