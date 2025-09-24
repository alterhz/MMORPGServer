package org.game.test.net.handler;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.message.ProtoListener;
import org.game.core.net.Message;
import org.game.proto.login.*;
import org.game.proto.scene.*;
import org.game.test.net.ClientProtoDispatcher;
import org.game.test.net.TokenData;

import java.util.concurrent.TimeUnit;

public class LoginHandler extends ClientProtoDispatcher {
    public static final Logger logger = LogManager.getLogger(LoginHandler.class);

    public LoginHandler()
    {
    }

    @ProtoListener(SCReconnect.class)
    public void onReconnect(Message message) {
        SCReconnect scReconnect = message.getProto(SCReconnect.class);
        logger.debug("收到重连结果:{}", scReconnect);

    }

    @ProtoListener(SCLogin.class)
    public void onLogin(Message message) {
        logger.info("登录成功");

        channel.eventLoop().schedule(() -> {
            // 请求获取角色列表
            CSQueryPlayer csQueryPlayers = new CSQueryPlayer();
            sendMessage(csQueryPlayers);
            logger.info("延迟2秒，请求角色列表");
        }, 2, TimeUnit.SECONDS);
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
                CSSelectPlayer csSelectPlayer = new CSSelectPlayer();
                long playerId = scQueryPlayers.getPlayer().get(0).getid();
                csSelectPlayer.setPlayerId(playerId);
                sendMessage(csSelectPlayer);

                TokenData.setPlayerId(playerId);
                logger.info("选择角色：{}", playerId);
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
        TokenData.setToken(scSendToClientEnd.getToken());
        logger.info("结束发送数据:{}", scSendToClientEnd);

        // 测试
        CSTest csTest = new CSTest();
        sendMessage(csTest);
    }

    

    // StageReadyNotify
    @ProtoListener(StageReadyNotify.class)
    public void onStageReadyNotify(Message message) {
        StageReadyNotify stageReadyNotify = message.getProto(StageReadyNotify.class);
        logger.info("场景准备就绪:stageSn={}", stageReadyNotify.getStageSn());

        EnterStageRequest enterStageRequest = new EnterStageRequest();
        sendMessage(enterStageRequest);
        logger.info("请求进入场景");
    }

    // SCEnterScene
    @ProtoListener(EnterStageResponse.class)
    public void onEnterStage(Message message) {
        // 处理消息
        EnterStageResponse enterStageResponse = message.getProto(EnterStageResponse.class);
        logger.info("进入场景成功:{}", enterStageResponse);

        // 移动到100,100
        UnitMoveRequest unitMoveRequest = new UnitMoveRequest();
        unitMoveRequest.setX(100);
        unitMoveRequest.setY(100);
        unitMoveRequest.setZ(0);
        sendMessage(unitMoveRequest);
    }

    // 测试
    @ProtoListener(SCTest.class)
    public void onTest(Message message) {
        // 处理消息
        SCTest scTest = message.getProto(SCTest.class);
        logger.info("测试结果：{}", scTest.getMessage());
    }

    // UnitAppearBroadcast
    @ProtoListener(UnitAppearBroadcast.class)
    public void onUnitAppear(Message message) {
        UnitAppearBroadcast unitAppear = message.getProto(UnitAppearBroadcast.class);
        logger.info("单位出现:{}", unitAppear.getUnits());
    }

    // UnitDisappearBroadcast
    @ProtoListener(UnitDisappearBroadcast.class)
    public void onUnitDisappear(Message message) {
        UnitDisappearBroadcast unitDisappear = message.getProto(UnitDisappearBroadcast.class);
        logger.info("单位消失:{}", unitDisappear.getUnitIds());
    }

    // UnitMoveBroadcast
    @ProtoListener(UnitMoveBroadcast.class)
    public void onUnitMoveBroadcast(Message message) {
        UnitMoveBroadcast unitMove = message.getProto(UnitMoveBroadcast.class);
        logger.info("单位移动广播: unitId={}, x={}, y={}, z={}", unitMove.getUnitId(),
                unitMove.getPosition().get(0).getX(),
                unitMove.getPosition().get(0).getY(),
                unitMove.getPosition().get(0).getZ());
    }

    // UnitMoveResponse
    @ProtoListener(UnitMoveResponse.class)
    public void onUnitMoveResponse(Message message) {
        UnitMoveResponse unitMoveResponse = message.getProto(UnitMoveResponse.class);
        logger.info("单位移动响应: fix={}, position={}", unitMoveResponse.getFix(), unitMoveResponse.getPosition());
    }

}
