package org.game.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.human.HumanObject;
import org.game.rpc.IHumanService;

public class HumanService extends GameServiceBase implements IHumanService {

    public static final Logger logger = LogManager.getLogger(HumanService.class);

    private final HumanObject humanObj;

    public HumanService(String id, HumanObject humanObj) {
        super(id);
        this.humanObj = humanObj;
    }

    public HumanObject getHumanObj() {
        return humanObj;
    }

    @Override
    public void init() {
        // 初始化角色服务
        logger.info("HumanObjectService 初始化");
    }

    @Override
    public void startup() {
        // 启动角色服务
        logger.info("HumanObjectService 启动");
        // 可以在这里启动定时任务等
    }

    @Override
    public void pulse(long now) {
        humanObj.pulse(now);
    }

    @Override
    public void destroy() {
        // 销毁角色服务
        logger.info("HumanObjectService 销毁");
        // 可以在这里保存角色数据等
    }

    @Override
    public void hotfix(Param param) {
        logger.info("HumanObjectService 热修复: param={}", param);
    }
}

