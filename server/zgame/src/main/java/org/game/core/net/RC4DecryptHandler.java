package org.game.core.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * RC4解密处理器
 */
public class RC4DecryptHandler extends ChannelInboundHandlerAdapter {
    private RC4 rc4;

    public RC4DecryptHandler(String key) {
        this.rc4 = new RC4(key);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            ReferenceCountUtil.release(byteBuf);

            // 解密数据
            byte[] decrypted = rc4.decrypt(data);
            ctx.fireChannelRead(decrypted);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}