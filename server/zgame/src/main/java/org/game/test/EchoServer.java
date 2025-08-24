package org.game.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.game.BaseUtils;
import org.game.LogCore;

public class EchoServer {

    public static void main(String[] args) {
        BaseUtils.init(20001);

        LogCore.test();
        LogCore.logger.info("游戏服务器启动中...");

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            LogCore.logger.debug("收到客户端消息: {}", msg);
                                            ctx.writeAndFlush("Echo: " + msg);
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            LogCore.logger.error("网络异常", cause);
                                            ctx.close();
                                        }
                                    }
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(18001).sync();
            LogCore.logger.info("游戏服务器已启动，监听端口: 18001");

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LogCore.logger.error("服务器启动失败", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Jar包提取函数
     * @param jarPath 要解压的jar文件路径
     * @param destDirectory 目标目录
     */
    public static void extractJar(String jarPath, String destDirectory) {
        // 这里添加具体的Jar解压实现代码
        // 可以使用java.util.jar.JarInputStream来实现
        // 确保处理目录结构还原、自动创建父目录等要求
    }
}