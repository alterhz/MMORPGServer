package org.game.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.game.BaseUtils;
import org.game.LogCore;

import java.util.Arrays;

/**
 * 基于Netty的网络服务器实现
 * <p>
 * 功能描述：
 * 1. 使用NIO，2个EventLoopGroup
 * 2. 消息粘包处理LengthFieldBasedFrameDecode解析：消息长度4字节=消息长度4字节+协议ID编号4字节+消息内容长度
 * 3. 协议加密和解密使用RC4
 * 4. 使用零拷贝和减少内存复制
 * 5. 创建ServerHandler，
 *    - 建立连接，打印远端IP
 *    - 断开连接打印日志
 *    - 异常处理
 * </p>
 *
 * @author Lingma
 * @date 2025-08-01
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20001);

        NettyServer server = new NettyServer(1080, "your_rc4_key");
        server.start();
    }

    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private String rc4Key;

    public NettyServer(int port, String rc4Key) {
        this.port = port;
        this.rc4Key = rc4Key;
    }

    /**
     * 启动Netty服务器
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加长度字段解码器处理粘包问题
                            // 参数说明：最大帧长度，长度字段偏移量，长度字段长度，长度字段调整值，初始字节剥离数
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4));
                            
                            // 添加RC4解密处理器
                            pipeline.addLast(new RC4DecryptHandler(rc4Key));
                            
                            // 添加业务处理器
                            pipeline.addLast(new ServerHandler());
                            
                            // 添加RC4加密处理器（用于出站数据）
                            pipeline.addLast(new RC4EncryptHandler(rc4Key));
                            
                            // 添加长度字段编码器
                            pipeline.addLast(new LengthFieldPrepender(4));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            LogCore.logger.info("Netty服务器已启动，监听端口: {}", port);
            
            // 等待服务器通道关闭
            serverChannel.closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    /**
     * 关闭服务器
     */
    public void shutdown() {
        LogCore.logger.info("正在关闭Netty服务器...");
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        LogCore.logger.info("Netty服务器已关闭");
    }

    /**
     * 服务器处理器，处理连接建立、断开和异常
     */
    private static class ServerHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            String remoteAddress = ctx.channel().remoteAddress().toString();
            LogCore.logger.info("客户端已连接: {}", remoteAddress);
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            String remoteAddress = ctx.channel().remoteAddress().toString();
            LogCore.logger.info("客户端已断开连接: {}", remoteAddress);
            ctx.fireChannelInactive();
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

                // 前4个字节是协议ID
                byte[] protocolIdBytes = Arrays.copyOfRange(data, 0, 4);
                int protocolId = bytesToInt(protocolIdBytes);
                
                // 剩余部分是消息内容
                byte[] content = Arrays.copyOfRange(data, 4, data.length);
                
                LogCore.logger.debug("收到协议ID: {}, 内容长度: {}", protocolId, content.length);
                
                // 这里可以添加具体的业务处理逻辑
                // 例如：根据协议ID分发到不同的处理函数
                
                // 简单回写示例
                byte[] responseData = new byte[4 + content.length];
                System.arraycopy(protocolIdBytes, 0, responseData, 0, 4);
                System.arraycopy(content, 0, responseData, 4, content.length);
                ctx.writeAndFlush(responseData);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LogCore.logger.error("网络异常: {}", ctx.channel().remoteAddress(), cause);
            ctx.close();
        }
        
        /**
         * 将4字节转换为int
         */
        private int bytesToInt(byte[] bytes) {
            return ((bytes[0] & 0xFF) << 24) |
                   ((bytes[1] & 0xFF) << 16) |
                   ((bytes[2] & 0xFF) << 8)  |
                   (bytes[3] & 0xFF);
        }
    }

    /**
     * RC4解密处理器
     */
    private static class RC4DecryptHandler extends ChannelInboundHandlerAdapter {
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
                
                // 解密数据
                byte[] decrypted = rc4.decrypt(data);
                ctx.fireChannelRead(decrypted);
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }

    /**
     * RC4加密处理器
     */
    private static class RC4EncryptHandler extends ChannelOutboundHandlerAdapter {
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
                ctx.write(Unpooled.wrappedBuffer(encrypted), promise);
            } else {
                ctx.write(msg, promise);
            }
        }
    }

    /**
     * RC4加密算法实现
     */
    private static class RC4 {
        private byte[] S;
        private static final String ALGORITHM = "RC4";

        public RC4(String key) {
            S = new byte[256];
            initialize(key.getBytes());
        }

        private void initialize(byte[] key) {
            int keyLen = key.length;
            byte[] T = new byte[256];
            
            // 初始化S和T数组
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % keyLen];
            }
            
            // 初始排列
            int j = 0;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                swap(S, i, j);
            }
        }

        public byte[] encrypt(byte[] plaintext) {
            return crypt(plaintext);
        }

        public byte[] decrypt(byte[] ciphertext) {
            return crypt(ciphertext); // RC4加密和解密是相同的操作
        }

        private byte[] crypt(byte[] data) {
            byte[] result = new byte[data.length];
            int i = 0, j = 0;
            
            for (int k = 0; k < data.length; k++) {
                i = (i + 1) & 0xFF;
                j = (j + S[i]) & 0xFF;
                swap(S, i, j);
                
                int t = (S[i] + S[j]) & 0xFF;
                result[k] = (byte) (data[k] ^ S[t]);
            }
            
            return result;
        }

        private void swap(byte[] array, int i, int j) {
            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}