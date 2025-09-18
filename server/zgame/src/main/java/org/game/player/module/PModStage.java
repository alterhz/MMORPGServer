package org.game.player.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.Param;
import org.game.core.event.EventListener;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.global.rpc.IStageGlobalService;
import org.game.player.PlayerModBase;
import org.game.player.PlayerObject;
import org.game.player.event.OnPlayerLoadComplete;
import org.game.stage.human.HumanObjectData;
import org.game.stage.rpc.IStageObjectService;

import java.util.concurrent.CompletableFuture;

public class PModStage extends PlayerModBase {
    public static final Logger logger = LogManager.getLogger(PModStage.class);

    public PModStage(PlayerObject humanObj) {
        super(humanObj);
    }

    @EventListener
    public void EnterStageAfterLogin(OnPlayerLoadComplete onPlayerLoadComplete) {
        logger.info("角色加载完成，准备进入场景");

        // 获取场景全局服务
        IStageGlobalService stageGlobalService = ReferenceFactory.getProxy(IStageGlobalService.class);

        // 请求进入默认场景（示例中使用场景SN为1）
        CompletableFuture<Param> stageFuture = stageGlobalService.getStageInfo(101, 0);
        stageFuture.thenAccept(param -> {
            if (param.containsKey("error")) {
                logger.error("获取场景信息失败: {}", param);
                return;
            }

            long stageId = param.getLong("stageId");
            int stageSn = param.getInt("stageSn");
            ToPoint stageToPoint = param.get("toPoint");

            HumanObjectData humanObjectData = new HumanObjectData();
            humanObjectData.setPlayerId(playerObj.getId());
            humanObjectData.setClientPoint(playerObj.getClientPoint());

            IStageObjectService stageObjectService = ReferenceFactory.getProxy(IStageObjectService.class, stageToPoint);
            stageObjectService.registerStageHuman(humanObjectData);





            logger.info("玩家进入场景成功: stageId={}, stageSn={}", stageId, stageSn);
        }).exceptionally(throwable -> {
            logger.error("进入场景失败", throwable);
            return null;
        });
    }
}