package org.game.test.net.handler;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.message.ProtoListener;
import org.game.core.net.Message;
import org.game.proto.login.*;
import org.game.proto.scene.CSEnterScene;
import org.game.proto.scene.SCEnterScene;
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
        CSQueryPlayer csQueryPlayers = new CSQueryPlayer();
        sendMessage(csQueryPlayers);
    }

    @ProtoListener(SCQueryPlayer.class)
    public void onQueryPlayers(Message message) {
        // 处理消息
        SCQueryPlayer scQueryPlayers = message.getProto(SCQueryPlayer.class);
        if (scQueryPlayers.getCode() == 0) {
            if (scQueryPlayers.getPlayer().isEmpty()) {
                // 创角
                logger.info("创建角色");
                CSCreatePlayer csCreatePlayer = new CSCreatePlayer();
                // 随机一个名称
                csCreatePlayer.setName("测试" + RandomUtils.nextInt(1, 9999));
                csCreatePlayer.setProfession("magic");
                sendMessage(csCreatePlayer);
            } else {
                logger.info("选择角色");
                CSSelectPlayer csSelectPlayer = new CSSelectPlayer();
                csSelectPlayer.setPlayerId(scQueryPlayers.getPlayer().get(0).getid());
                sendMessage(csSelectPlayer);
            }
        }
    }

    @ProtoListener(SCCreatePlayer.class)
    public void onCreatePlayer(Message message) {
        // 处理消息
        SCCreatePlayer scCreatePlayer = message.getProto(SCCreatePlayer.class);
        if (scCreatePlayer.getSuccess()) {
            logger.info("创建角色成功");
        } else {
            logger.info("创建角色失败");
        }
    }

    // 选择角色成功
    @ProtoListener(SCSelectPlayer.class)
    public void onSelectPlayer(Message message) {
        // 处理消息
        SCSelectPlayer scSelectPlayer = message.getProto(SCSelectPlayer.class);
        if (scSelectPlayer.getCode() == 0) {
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
        logger.info("开始发送数据{}", scSendToClientBegin);
    }

    // SCSendToClientEnd
    @ProtoListener(SCSendToClientEnd.class)
    public void onSendToClientEnd(Message message) {
        // 处理消息
        SCSendToClientEnd scSendToClientEnd = message.getProto(SCSendToClientEnd.class);
        logger.info("结束发送数据:{}", scSendToClientEnd);

        // 测试
        CSTest csTest = new CSTest();
        sendMessage(csTest);
    }


    // SCEnterScene
    @ProtoListener(SCEnterScene.class)
    public void onEnterScene(Message message) {
        // 处理消息
        SCEnterScene scEnterScene = message.getProto(SCEnterScene.class);
        logger.info("进入场景成功:{}", scEnterScene);

        CSEnterScene csEnterScene = new CSEnterScene();
        sendMessage(csEnterScene);
    }

    // 测试
    @ProtoListener(SCTest.class)
    public void onTest(Message message) {
        // 处理消息
        SCTest scTest = message.getProto(SCTest.class);
        logger.info("测试结果：{}", scTest.getMessage());
    }

}
