package org.game.test;

import org.game.BaseUtils;
import org.game.core.net.NettyServer;

public class NettyTest {
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20001);

        NettyServer server = new NettyServer(1080, "your_rc4_key");
        server.start();

        // 等待3秒
        Thread.sleep(3000);

        NettyClient client = new NettyClient("127.0.0.1", 1080, "your_rc4_key");
        client.start();

        server.shutdown();
    }
}
