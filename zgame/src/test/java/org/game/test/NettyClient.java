package org.game.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.game.BaseUtils;
import org.game.LogCore;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Netty客户端测试类
 * 用于测试与NettyServer的连接和通信
 * 
 * @author Lingma
 * @date 2025-08-01
 */
public class NettyClient {
    
    private final String host;
    private final int port;
    private final String rc4Key;
    private EventLoopGroup group;
    private Channel channel;
    
    public NettyClient(String host, int port, String rc4Key) {
        this.host = host;
        this.port = port;
        this.rc4Key = rc4Key;
    }
    
    /**
     * 启动客户端并连接到服务器
     * 
     * @throws InterruptedException 如果连接过程中线程被中断
     */
    public void start() throws InterruptedException {
        group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 添加长度字段解码器处理粘包问题
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));
                            
                            // 添加RC4解密处理器
                            pipeline.addLast(new RC4DecryptHandler(rc4Key));
                            
                            // 添加客户端业务处理器
                            pipeline.addLast(new ClientHandler());
                            
                            // 添加RC4加密处理器（用于出站数据）
                            pipeline.addLast(new RC4EncryptHandler(rc4Key));
                            
                            // 添加长度字段编码器
                            pipeline.addLast(new LengthFieldPrepender(4));
                        }
                    });
            
            // 连接到服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
            LogCore.logger.info("已连接到服务器 {}:{}", host, port);
            
            // 等待用户输入并发送消息
            handleUserInput();
            
            // 等待连接关闭
            channel.closeFuture().sync();
        } finally {
            shutdown();
        }
    }
    
    /**
     * 处理用户输入，发送消息到服务器
     */
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要发送到服务器的消息 (输入 'quit' 退出):");
        
        new Thread(() -> {
            while (true) {
                String input = scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)) {
                    channel.close();
                    break;
                }
                
                if (channel.isActive()) {
                    // 创建测试消息，确保格式与服务端一致
                    byte[] content = input.getBytes();
                    byte[] protocolId = intToBytes(1001); // 使用协议ID 1作为测试
                    byte[] message = new byte[4 + content.length];
                    System.arraycopy(protocolId, 0, message, 0, 4);
                    System.arraycopy(content, 0, message, 4, content.length);
                    
                    // 使用ByteBuf发送消息
                    channel.writeAndFlush(message);
                }
            }
        }).start();
    }
    
    /**
     * 关闭客户端
     */
    public void shutdown() {
        LogCore.logger.info("正在关闭客户端...");
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        LogCore.logger.info("客户端已关闭");
    }
    
    /**
     * 客户端处理器，处理从服务器接收到的消息
     */
    private static class ClientHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof byte[]) {
                byte[] data = (byte[]) msg;
                // 如果数据长度小于4字节，则无法提取协议ID
                if (data.length < 4) {
                    LogCore.logger.warn("接收到无效数据，长度不足: {}", data.length);
                    return;
                }

                // 前4个字节是协议ID
                byte[] protocolIdBytes = java.util.Arrays.copyOfRange(data, 0, 4);
                int protocolId = bytesToInt(protocolIdBytes);
                
                // 剩余部分是消息内容
                byte[] content = java.util.Arrays.copyOfRange(data, 4, data.length);
                
                LogCore.logger.info("收到服务器响应 - 协议ID: {}, 内容长度: {}", protocolId, content.length);
                System.out.println("服务器响应: " + new String(content));
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LogCore.logger.error("客户端异常", cause);
            ctx.close();
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
    
    /**
     * 将int转换为4字节
     */
    private static byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }
    
    /**
     * 将4字节转换为int
     */
    private static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8)  |
               (bytes[3] & 0xFF);
    }
    
    public static void main(String[] args) {
        BaseUtils.init(20001);
        try {
            NettyClient client = new NettyClient("localhost", 1080, "your_rc4_key");
            client.start();
        } catch (InterruptedException e) {
            LogCore.logger.error("客户端启动失败", e);
        }
    }
}