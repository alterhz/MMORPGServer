package org.game.test.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.game.LogCore;
import org.game.core.net.Message;
import org.game.core.net.RC4;

/**
 * 客户端处理
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private MessageHandler messageHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogCore.logger.info("与服务器建立连接: {}", ctx.channel().remoteAddress());
        messageHandler = new MessageHandler(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof byte[]) {
            byte[] data = (byte[]) msg;
            // 如果数据长度小于4字节，则无法提取协议ID
            if (data.length < 4) {
                LogCore.logger.warn("接收到无效数据，长度不足: {}", data.length);
                return;
            }

            Message message = Message.fromBytes(data);
            messageHandler.handle(ctx.channel(), message);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogCore.logger.info("与服务器断开连接: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogCore.logger.error("客户端发生异常", cause);
        ctx.close();
    }
}