package org.game.test.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.game.LogCore;
import org.game.core.net.Message;

import java.io.IOException;

/**
 * 客户端处理
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final ClientMessageDispatcher clientMessageDispatcher = new ClientMessageDispatcher();

    private LoginMessageHandler loginMessageHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogCore.logger.info("与服务器建立连接: {}", ctx.channel().remoteAddress());
        clientMessageDispatcher.init();
        loginMessageHandler = new LoginMessageHandler(ctx.channel());
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
            clientMessageDispatcher.dispatch(String.valueOf(message.getProtoID()), loginMessageHandler, message);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogCore.logger.info("与服务器断开连接: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogCore.logger.error("客户端发生异常", cause);
        if (cause instanceof IOException &&
                cause.getMessage().contains("远程主机强迫关闭了一个现有的连接")) {
            LogCore.logger.debug("客户端主动断开连接: {}", ctx.channel().remoteAddress());
        } else {
            LogCore.logger.error("网络异常: {}", ctx.channel().remoteAddress(), cause);
        }
        ctx.close();
    }
}