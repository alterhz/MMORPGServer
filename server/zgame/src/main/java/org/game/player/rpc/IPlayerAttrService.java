package org.game.player.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.player.module.MyStruct;

@HumanRPCProxy
public interface IPlayerAttrService {
    void test(String value, MyStruct myStruct);
}
