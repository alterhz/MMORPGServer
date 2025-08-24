package org.game.core.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * RC4加密处理器
 */
public class RC4EncryptHandler extends ChannelOutboundHandlerAdapter {
    private RC4 rc4;

    public RC4EncryptHandler(String key) {
        this.rc4 = new RC4(key);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof byte[]) {
            byte[] data = (byte[]) msg;
            // 加密数据
            byte[] encrypted = rc4.encrypt(data);
            ByteBuf buffer = ctx.alloc().buffer(encrypted.length);
            buffer.writeBytes(encrypted);
            ctx.write(buffer, promise);
        } else {
            ctx.write(msg, promise);
        }
    }
}