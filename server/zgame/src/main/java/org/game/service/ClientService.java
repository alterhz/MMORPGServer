package org.game.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.*;
import org.game.core.net.Message;
import org.game.core.rpc.FromPoint;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.proto.UserCredentials;
import org.game.rpc.IClientService;
import org.game.rpc.ILoginService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 客户端服务实现类
 * <p>
 * 负责处理客户端相关的业务逻辑，包括：
 * 1. 热更新功能 - 通过hotfix方法实现
 * 2. 消息发送功能 - 通过sendMsg方法实现
 * </p>
 * 继承自GameServiceBase，具备游戏服务的基本功能和生命周期管理
 * 实现了IClientService接口定义的业务方法
 */
public class ClientService  extends GameServiceBase implements IClientService {

    public static final Logger logger = LogManager.getLogger(ClientService.class);

    private final Channel channel;

    private final int clientID;

    private TickTimer delayCloseTimer;

    // 添加消息队列，用于存储待处理的消息结构体
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    public ClientService(int clientID, Channel channel) {
        super(String.valueOf(clientID));
        this.channel = channel;
        this.clientID = clientID;
    }

    public int getClientID() {
        return clientID;
    }

    @Override
    public void init() {
        logger.info("ClientService 初始化, id={}", getName());

//        timerQueue.createTimer(5000, 2000, (id, context) -> {
//            // 发送消息
//            sendMessage(Message.createMessage(1, "hello world"));
//        });
    }

    @Override
    public void startup() {
        logger.info("ClientService 启动, id={}", getName());
    }

    public void delayClose() {
        logger.info("ClientService 关闭, id={}", getName());
        if (delayCloseTimer != null) {
            return;
        }
        delayCloseTimer = new TickTimer(2000);
    }

    @Override
    public void pulse(long now) {
        // 在心跳中消费消息队列中的消息
        consumeMessages();
        
        if (delayCloseTimer != null && delayCloseTimer.update(now)) {
            gameThread.removeGameService(this);
            destroy();
        }
    }

    @Override
    public void destroy() {
        logger.info("ClientService 销毁, id={}", getName());
    }

    @Override
    public void hotfix(Param param) {

    }

    @Override
    public void sendMessage(Message message) {
        // message转为byte[]
        byte[] bytes = message.toBytes();

        ChannelFuture channelFuture = channel.writeAndFlush(bytes);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("ClientService 发送消息成功, id={}, msgId={}", getName(), message.getProtoID());
            } else {
                logger.error("ClientService 发送消息失败, id={}, msgId={}", getName(), message.getProtoID(), future.cause());
            }
        });
    }

    public void onReceiveMessage(Message message) {
        // 将消息结构体添加到队列中，而不是直接处理
        try {
            boolean offer = messageQueue.offer(message);
            logger.info("ClientService 添加消息到队列, id={}, protoID={}, queueSize={}, offer={}", getName(), message.getProtoID(), messageQueue.size(), offer);
        } catch (Exception e) {
            logger.error("ClientService 添加消息失败, id={}", getName(), e);
        }
    }
    
    /**
     * 消费消息队列中的消息
     * 在pulse方法中被调用，确保在游戏线程中处理消息
     */
    private void consumeMessages() {
        // 每次心跳处理最多300条消息，避免占用过多处理时间
        Message message;
        
        for (int i = 0; i < 300; i++) {
            message = messageQueue.poll();
            if (message == null) {
                break;
            }
            // 直接打印消息内容
            logger.info("ClientService 消费消息, id={}, protoID={}, content={}", getName(), message.getProtoID(), message.getJsonStr());

            onDispatchMessage(message);
        }
    }

    private void onDispatchMessage(Message message) {
        switch (message.getProtoID()) {
            case 1001:
                // 处理登录请求
                UserCredentials credentials = message.getJsonObject(UserCredentials.class);
                logger.info("ClientService 登录请求, id={}, username={}, password={}", getName(), credentials.getUsername(), credentials.getPassword());

                ToPoint fromPoint = new ToPoint(GameProcess.getGameProcessName(), GameThread.getCurrentThreadName(), getName());

                ILoginService loginService = ReferenceFactory.getProxy(ILoginService.class);
                loginService.login(credentials.getUsername(), credentials.getPassword(), fromPoint);
                break;
            case 2:
                // 处理消息2
                break;
            default:
                // 处理其他消息
                break;
        }
    }
}