package org.game.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.game.BaseUtils;
import org.game.LogCore;
import org.game.core.net.*;
import org.game.proto.CSLogin;

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
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 4, 0, 4));
                            
                            // 添加RC4解密处理器
                            pipeline.addLast(new RC4DecryptHandler(rc4Key));
                            
                            // 添加客户端业务处理器
                            pipeline.addLast(new ClientHandler());
                            
                            // 添加长度字段编码器
                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 添加RC4加密处理器（用于出站数据）
                            pipeline.addLast(new RC4EncryptHandler(rc4Key));
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
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            try {
                while (channel.isOpen()) {
                    System.out.print("请输入要发送的消息（输入'quit'退出）: ");
                    String input = scanner.nextLine();
                    if ("quit".equals(input)) {
                        channel.close();
                        break;
                    }
                    
                    // 构造测试消息包：协议ID(4字节)+内容
                    byte[] content = input.getBytes();
                    String name = new String(content);

                    CSLogin CSLogin = new CSLogin(name, "admin");

                    Message message = Message.createMessage(1001, CSLogin);
                    content = message.toBytes();

                    // 发送消息
                    channel.writeAndFlush(content).addListener(future -> {
                        if (!future.isSuccess()) {
                            LogCore.logger.error("发送消息失败", future.cause());
                        }
                    });
                    
                    // 等待一段时间避免过快输入
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (Exception e) {
                LogCore.logger.error("处理用户输入时发生错误", e);
            } finally {
                scanner.close();
            }
        }).start();
    }
    
    /**
     * 关闭客户端资源
     */
    public void shutdown() {
        LogCore.logger.info("正在关闭客户端...");
        try {
            if (channel != null) {
                channel.close().sync();
            }
        } catch (InterruptedException e) {
            LogCore.logger.error("关闭channel时被中断", e);
        } finally {
            if (group != null) {
                group.shutdownGracefully();
            }
        }
        LogCore.logger.info("客户端已关闭");
    }
    
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20000);
        NettyClient client = new NettyClient("127.0.0.1", 1080, "your_rc4_key");
        client.start();
    }

}