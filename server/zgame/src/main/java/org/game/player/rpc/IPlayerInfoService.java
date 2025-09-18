package org.game.player.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.player.module.MyStruct;

import java.util.concurrent.CompletableFuture;

@HumanRPCProxy
public interface IPlayerInfoService {

    CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct);
}
