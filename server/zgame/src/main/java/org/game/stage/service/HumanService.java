package org.game.stage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.message.ProtoScanner;
import org.game.core.net.Message;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.core.stage.HumanProtoDispatcher;
import org.game.player.rpc.IPlayerInfoService;
import org.game.stage.human.module.HModStage;
import org.game.stage.rpc.IHumanService;
import org.game.stage.human.HumanObject;

public class HumanService extends GameServiceBase implements IHumanService {

    public static final Logger logger = LogManager.getLogger(HumanService.class);

    private final HumanObject humanObj;
    
    public HumanService(HumanObject humanObj) {
        super(String.valueOf(humanObj.getUnitId()));
        this.humanObj = humanObj;
    }

    public HumanObject getHumanObj() {
        return humanObj;
    }

    @Override
    public void init() {
        logger.info("HumanService 初始化. stageId={}", getName());
        humanObj.init();
    }

    @Override
    public void startup() {
        logger.info("HumanObjectService 启动. stageId={}", getName());
    }

    @Override
    public void destroy() {
        logger.info("HumanObjectService 销毁. stageId={}", getName());
    }

    @Override
    public void hotfix(Param param) {
        logger.info("HumanObjectService 热修复: param={}, stageId={}", param, getName());
    }

    @Override
    public void dispatchProto(Message message) {
        int protoID = message.getProtoID();

        Class<?> protoClass = ProtoScanner.getProtoClass(protoID);
        Object protoObj = message.getProto(protoClass);
        if (protoObj == null) {
            logger.error("StageHuman接收到协议，解析失败。protoID={}, stageHumanObj={}", protoID, humanObj);
            return;
        }

        HumanProtoDispatcher.getInstance().dispatch(String.valueOf(protoID), method ->  {
            Class<?> hModClass = method.getDeclaringClass();
            return humanObj.getModBase(hModClass);
        }, protoObj);
    }

    @Override
    public void humanLeaveStage() {
        logger.info("HumanLeaveStage，保存坐标信息");
        humanObj.getMod(HModStage.class).savePosition();

        getGameThread().runTask(() -> {
            getGameThread().removeGameService(HumanService.this);
        });
    }

    @Override
    public void reconnect(ToPoint clientPoint) {
        humanObj.setClientPoint(clientPoint);
        logger.info("HumanService 重连: {}", clientPoint);
    }

}