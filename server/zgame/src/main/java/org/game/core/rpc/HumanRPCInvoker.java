package org.game.core.rpc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameThread;
import org.game.core.ServiceRegistryManager;
import org.game.core.human.PlayerLookup;
import org.game.core.human.PlayerThread;
import org.game.core.utils.JsonUtils;
import org.game.player.rpc.IPlayerService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HumanRPCInvoker implements InvocationHandler {

    private static final Logger logger = LogManager.getLogger(HumanRPCInvoker.class);

    private final long playerId;

    HumanRPCInvoker(long playerId) {
        this.playerId = playerId;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 排除Object类的方法
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        // 获取接口类
        Class<?> interfaceClass = method.getDeclaringClass();

        // 获取服务配置注解
        HumanRPCProxy rpcProxy = interfaceClass.getAnnotation(HumanRPCProxy.class);
        if (rpcProxy == null) {
            throw new RuntimeException("Service implementation not found for interface: " + interfaceClass.getName());
        }

        GameThread currentGameThread = GameThread.getCurrentGameThread();
        if (currentGameThread == null) {
            throw new RuntimeException("No game thread found for current thread: " + Thread.currentThread().getName());
        }

        // 获取服务名称，接口名称（不包含包名）
        String serviceName = interfaceClass.getSimpleName().toLowerCase();

        String gameProcessName = GameProcess.getName();
        String currentThreadName = GameThread.getCurrentThreadName();

        // 构造FromPoint
        FromPoint fromPoint = new FromPoint(gameProcessName, currentThreadName); // 实际应用中需要获取当前进程和线程名

        // 如果ToPoint为空，则创建一个默认的ToPoint
        ToPoint targetPoint = new ToPoint(gameProcessName, PlayerThread.NAME, String.valueOf(playerId));

        // 构造参数类型列表
        Class<?>[] paramTypes = method.getParameterTypes();
        List<String> parameterTypes = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (args != null) {
            for (Class<?> paramType : paramTypes) {
                parameterTypes.add(paramType.getName());
            }
            parameters.addAll(Arrays.asList(args));
        }

        List<Object> dispatchMethodArgs = new ArrayList<>();
        dispatchMethodArgs.add(serviceName);
        dispatchMethodArgs.add(method.getName());
        dispatchMethodArgs.add(parameters);
        dispatchMethodArgs.add(parameterTypes);

        // humanServiceMethodArgs转为parameterTypes
        List< String> dispatchMethodArgsTypes = new ArrayList<>();
        for (Object param : dispatchMethodArgs) {
            dispatchMethodArgsTypes.add(param.getClass().getName());
        }


        // 构造RpcInvocation
        RpcInvocation invocation = new RpcInvocation(fromPoint, targetPoint, IPlayerService.DISPATCH_METHOD_NAME, dispatchMethodArgs, dispatchMethodArgsTypes);

        // 生成唯一请求ID
        String requestId = UUID.randomUUID().toString();

        // 构造RPCRequest
        RPCRequest request = new RPCRequest(requestId, invocation);

        // 创建CompletableFuture用于回调
        CompletableFuture<Object> future = new CompletableFuture<>();
        currentGameThread.addCallback(request, future);

        // TODO 判断是否为本地调用
        boolean isLocal = false;
        if (isLocal) {
            String jsonRequest = JsonUtils.encode(request);
            if (jsonRequest == null) {
                // 如果序列化失败，移除回调并抛出异常
                currentGameThread.removeCallback(request.getRequestId());
                logger.error("Failed to serialize RPC request = {}", request);
                throw new RuntimeException("Failed to serialize RPC request");
            }

            // 发送序列化后的请求到目标GameThread
            parseRPCRequest(jsonRequest);
        } else {
            dispatchRPCRequest(request);
        }

        return future;
    }

    /**
     * 发送请求到目标GameThread
     * @param jsonRequest 序列化后的请求
     */
    private void parseRPCRequest(String jsonRequest) {
        // jackson反序列化jsonRequest
        RPCRequest request = JsonUtils.tryDecode(jsonRequest, RPCRequest.class);
        if (request == null) {
            logger.error("Failed to deserialize RPC request: {}", jsonRequest);
            return;
        }

        dispatchRPCRequest(request);
    }

    /**
     * 分发RPC请求
     * <p>查找GameThread线程，添加到rpcRequestQueue列表</p>
     * @param request RPC请求
     */
    public void dispatchRPCRequest(RPCRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Dispatching RPC request: {}", request);
        }

        // 获取目标线程名称
        String targetThreadName = request.getInvocation().getToPoint().getGameThreadName();

        // targetThreadName为空，则通过服务名称查找线程
        if (StringUtils.isBlank(targetThreadName)) {
            String gameServiceName = request.getInvocation().getToPoint().getGameServiceName();
            targetThreadName = ServiceRegistryManager.getServiceGameThreadName(gameServiceName);
        } else if (targetThreadName.equals(PlayerThread.NAME)) {
            // 查找humanId所在的GameThread
            long playerId = NumberUtils.toLong(request.getInvocation().getToPoint().getGameServiceName());
            targetThreadName = PlayerLookup.getPlayerThreadName(playerId);
        }

        // 通过GameProcess获取目标GameThread
        GameThread targetThread = GameProcess.getGameThread(targetThreadName);

        // 如果找到了目标线程，则将请求添加到其rpcRequestQueue中
        if (targetThread != null) {
            targetThread.addRPCRequest(request);
        } else {
            logger.warn("Target GameThread not found: {}", targetThreadName);
        }
    }
}
