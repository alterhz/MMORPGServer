package org.game.test.robot;

import org.game.BaseUtils;
import org.game.core.message.ProtoScanner;
import org.game.test.net.NettyClientAuto;
import org.game.test.net.handler.LoginHandler;

public class RobotLogin {
    public static void main(String[] args) throws InterruptedException {
        BaseUtils.init(20001);

        ProtoScanner.init();

        NettyClientAuto client = new NettyClientAuto("127.0.0.1", 1080, "your_rc4_key");
        client.start(new LoginHandler(), "robot01");
    }
}
