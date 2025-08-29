package org.game.test;

import org.game.BaseUtils;
import org.game.core.net.NettyServer;
import org.game.test.net.NettyClientInput;

public class NettyTest {
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20001);

        NettyServer server = new NettyServer(1080, "your_rc4_key");
        server.start();

        // 等待3秒
        Thread.sleep(3000);

        NettyClientInput client = new NettyClientInput("127.0.0.1", 1080, "your_rc4_key");
        client.start();

        server.shutdown();
    }
}
