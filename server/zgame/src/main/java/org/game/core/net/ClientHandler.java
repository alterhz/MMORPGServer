package org.game.core.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.game.LogCore;

/**
 * 客户端处理
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogCore.logger.info("与服务器建立连接: {}", ctx.channel().remoteAddress());
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

            // 前4个字节是协议ID
            int protocolId = RC4.bytesToInt(new byte[]{data[0], data[1], data[2], data[3]});

            // 剩余部分是消息内容
            int contentLength = data.length - 4;
            byte[] content = new byte[contentLength];
            System.arraycopy(data, 4, content, 0, contentLength);

            LogCore.logger.info("收到服务器消息 - 协议ID: {}, 内容: {}", protocolId, new String(content));
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