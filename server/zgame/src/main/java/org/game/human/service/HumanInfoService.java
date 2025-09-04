package org.game.human.service;

import org.game.core.rpc.HumanServiceBase;
import org.game.human.HumanObject;
import org.game.human.MyStruct;
import org.game.human.rpc.IHumanInfoService;

import java.util.concurrent.CompletableFuture;

public class HumanInfoService extends HumanServiceBase implements IHumanInfoService {
    public HumanInfoService(HumanObject humanObj) {
        super(humanObj);
    }

    @Override
    public CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct) {
        String id = humanObj.getId();
        return CompletableFuture.completedFuture("HumanInfoService" + id);
    }
}
