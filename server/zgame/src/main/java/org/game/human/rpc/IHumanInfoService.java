package org.game.human.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.human.MyStruct;

import java.util.concurrent.CompletableFuture;

@HumanRPCProxy
public interface IHumanInfoService {

    CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct);
}
