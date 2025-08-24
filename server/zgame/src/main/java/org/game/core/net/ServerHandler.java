package org.game.core.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.game.LogCore;
import org.game.service.ClientService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务器处理器，处理连接建立、断开和异常
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    // 用于分配连接的索引
    private static AtomicInteger channelAllocID = new AtomicInteger(0);

    private ClientService clientService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String remoteAddress = ctx.channel().remoteAddress().toString();

        int clientID = channelAllocID.incrementAndGet();
        int connThreadIndex = clientID % ConnThread.getConnThreadCount();
        ConnThread connThread = ConnThread.getConnThread(connThreadIndex);
        if (connThread == null) {
            LogCore.logger.error("找不到连接线程，请检查配置, connThreadIndex:{}", connThreadIndex);
            return;
        }
        clientService = new ClientService(clientID, ctx.channel());
        connThread.addGameService(clientService);

        connThread.runTask(() -> {
            clientService.init();
            clientService.startup();
        });

        LogCore.logger.info("新客户端已连接: {}, 分配ID: {}", remoteAddress, clientID);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String remoteAddress = ctx.channel().remoteAddress().toString();
        LogCore.logger.info("客户端已断开连接: {}, channelAllocationIndex={}", remoteAddress, channelAllocID);

        clientService.getGameThread().runTask(() -> {
            clientService.delayClose();
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理接收到的消息
        if (msg instanceof byte[]) {
            byte[] data = (byte[]) msg;
            // 如果数据长度小于4字节，则无法提取协议ID
            if (data.length < 4) {
                LogCore.logger.warn("接收到无效数据，长度不足: {}", data.length);
                return;
            }

            Message message = Message.fromBytes(data);

            LogCore.logger.debug("收到协议，message = {}", message);

            clientService.onReceiveMessage(message);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LogCore.logger.error("网络异常: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

}
