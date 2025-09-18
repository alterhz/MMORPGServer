package org.game.player.service;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.event.IEvent;
import org.game.core.human.PlayerProtoDispatcher;
import org.game.core.message.ProtoScanner;
import org.game.core.net.Message;
import org.game.core.rpc.HumanServiceBase;
import org.game.core.rpc.RpcInvocation;
import org.game.player.PlayerObject;
import org.game.player.rpc.IPlayerService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerService extends GameServiceBase implements IPlayerService {

    public static final Logger logger = LogManager.getLogger(PlayerService.class);

    private final PlayerObject humanObj;

    public PlayerService(PlayerObject humanObj) {
        super(humanObj.getId());
        this.humanObj = humanObj;
    }

    public PlayerObject getHumanObj() {
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
    public void onPulse(long now) {
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

    @Override
    public void dispatchProto(Message message) {
        int protoID = message.getProtoID();

        Class<?> protoClass = ProtoScanner.getProtoClass(protoID);
        Object protoObj = message.getProto(protoClass);
        if (protoObj == null) {
            logger.error("HumanObject接收到协议，解析失败。protoID={}, humanObj={}", protoID, humanObj);
            return;
        }

        PlayerProtoDispatcher.getInstance().dispatch(String.valueOf(protoID), method ->  {
            Class<?> hModClass = method.getDeclaringClass();
            return humanObj.getModBase(hModClass);
        }, protoObj);
    }


    @Override
    public void fireEvent(IEvent event) {
        humanObj.fireEvent(event);
    }


}

