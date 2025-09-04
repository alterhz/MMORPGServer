package org.game.human.service;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.net.Message;
import org.game.core.rpc.HumanServiceBase;
import org.game.core.rpc.RpcInvocation;
import org.game.human.HumanObject;
import org.game.human.rpc.IHumanService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HumanService extends GameServiceBase implements IHumanService {

    public static final Logger logger = LogManager.getLogger(HumanService.class);

    private final HumanObject humanObj;

    public HumanService(HumanObject humanObj) {
        super(humanObj.getId());
        this.humanObj = humanObj;
    }

    public HumanObject getHumanObj() {
        return humanObj;
    }

    @Override
    public void init() {
        // 初始化角色服务
        logger.info("HumanObjectService 初始化");
        humanObj.init();
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
    }

    @Override
    public void hotfix(Param param) {
        logger.info("HumanObjectService 热修复: param={}", param);
    }

    @Override
    public void dispatchProto(Message message) {

    }

    @Override
    public CompletableFuture<Object> dispatchRPC(String hModService, String methodName, List<Object> parameters, List<String> parameterTypes) {
        logger.info("HumanObjectService 调用: hModService={}, methodName={}, parameterTypes={}, parameters={}", hModService, methodName, parameterTypes, parameters);

        // 转发rpc调用
        HumanServiceBase humanService = humanObj.getHumanService(hModService);
        if (humanService == null) {
            logger.error("HumanObjectService 调用失败: hModService={}, methodName={}, parameterTypes={}, parameters={}", hModService, methodName, parameterTypes, parameters);
            return null;
        }

        List<Object> typedParameters = new ArrayList<>(parameters.size());
        for (int i = 0; i < parameters.size(); i++) {
            Object param = parameters.get(i);
            String typeName = parameterTypes.get(i);

            Object typedParam = RpcInvocation.convertToType(param, typeName);
            typedParameters.add(typedParam);
        }

        // 通过反射调用
        try {
            Object result = MethodUtils.invokeMethod(humanService, methodName, typedParameters.toArray());
            return (CompletableFuture<Object>) result;
        } catch (InvocationTargetException e) {
            logger.error("HumanObjectService 调用失败InvocationTargetException: hModService={}, methodName={}, parameterTypes={}, parameters={}", hModService, methodName, parameterTypes, parameters);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            logger.error("HumanObjectService 调用失败NoSuchMethodException: hModService={}, methodName={}, parameterTypes={}, parameters={}", hModService, methodName, parameterTypes, parameters);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.error("HumanObjectService 调用失败IllegalAccessException: hModService={}, methodName={}, parameterTypes={}, parameters={}", hModService, methodName, parameterTypes, parameters);
            throw new RuntimeException(e);
        }
    }
}

