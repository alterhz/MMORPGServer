package org.game.human.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.human.module.MyStruct;

@HumanRPCProxy
public interface IHumanAttrService {
    void test(String value, MyStruct myStruct);
}
