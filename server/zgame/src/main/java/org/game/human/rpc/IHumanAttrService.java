package org.game.human.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.human.MyStruct;

@HumanRPCProxy
public interface IHumanAttrService {
    void test(String value, MyStruct myStruct);
}
