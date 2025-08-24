package org.game.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.rpc.IHumanObjectService;

public class HumanObjectService extends GameServiceBase implements IHumanObjectService {

    public static final Logger logger = LogManager.getLogger(HumanObjectService.class);

    public HumanObjectService(String name) {
        super(name);
    }

    @Override
    public void init() {
        // 初始化角色服务
        logger.info("HumanObjectService 初始化");
        // 可以在这里加载角色数据等
    }

    @Override
    public void startup() {
        // 启动角色服务
        logger.info("HumanObjectService 启动");
        // 可以在这里启动定时任务等
    }

    @Override
    public void pulse(long now) {

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

