package org.game.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.rpc.RPCRequest;
import org.game.core.rpc.RPCResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class GameThread extends Thread {

    public static final Logger logger = LogManager.getLogger(GameThread.class);

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 线程变量，保存当前GameThread
     */
    private static final ThreadLocal<GameThread> localGameThread = new ThreadLocal<>();

    /**
     * 游戏服务列表
     */
    // 将List改为Map，以服务名称为key，服务实例为value
    private final Map<String, GameServiceBase> gameServices = new ConcurrentHashMap<>();

    /**
     * 任务队列
     */
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    // RPC请求队列
    private final ConcurrentLinkedQueue<RPCRequest> rpcRequestQueue = new ConcurrentLinkedQueue<>();

    // RPC响应队列
    private final ConcurrentLinkedQueue<RPCResponse> rpcResponseQueue = new ConcurrentLinkedQueue<>();

    // 存储CompletableFuture回调的Map
    private final ConcurrentHashMap<String, CompletableFuture<Object>> callbackMap = new ConcurrentHashMap<>();

    // 超时时间（毫秒）
    private static final long CALLBACK_TIMEOUT = 30000;

    /**
     * 定时器队列
     */
    private final TimerQueue callBackTimeoutQueue = new TimerQueue();

    public GameThread(String name) {
        setName(name);
        this.setDaemon(true); // 设置为守护线程
    }

    /**
     * 获取当前线程变量
     */
    public static GameThread getCurrentGameThread() {
        return localGameThread.get();
    }

    /**
     * 获取当前线程名称
     * <p>调用getCurrentGameThread获取线程，不存在返回""</p>
     * @return 当前线程名称
     */
    public static String getCurrentThreadName() {
        GameThread currentThread = getCurrentGameThread();
        if (currentThread == null) {
            throw new IllegalStateException("Current thread is not a GameThread");
        }
        return currentThread.getName();
    }

    @Override
    public void run() {
        localGameThread.set(this);

        while (!isInterrupted()) {
            try {
                long now = System.currentTimeMillis();
                // 执行任务队列中的任务
                executeTasks();

                // 执行心跳
                pulse(now);

                // 处理RPC
                processRPC();

                // 短暂休眠，防止CPU占用过高
                Thread.sleep(10);
            } catch (InterruptedException e) {
                interrupt();
                logger.error("GameThread运行异常", e);
            }
        }
    }

    // 异步执行任务
    public void runTask(Runnable task) {
        taskQueue.offer(task);
    }

    // 执行任务队列中的任务
    private void executeTasks() throws InterruptedException {
        Runnable task;
        while ((task = taskQueue.poll()) != null) {
            task.run();
        }
    }

    // 心跳方法
    private void pulse(long now) {
        // 执行所有GameService的心跳
        for (GameServiceBase service : gameServices.values()) {
            service.timerQueue.update(now);
            service.pulse(now);
        }

        callBackTimeoutQueue.update(now);
    }

    // 添加GameService
    public void addGameService(GameServiceBase service) {
        gameServices.put(service.getName(), service);
    }

    // 根据名称获取GameService
    public GameServiceBase getGameService(String name) {
        return gameServices.get(name);
    }

    // 获取所有GameService
    public List<GameServiceBase> getAllGameServices() {
        return new ArrayList<>(gameServices.values());
    }

    public void destroy() {

    }

    // RPC相关方法

    public void addRPCRequest(RPCRequest request) {
        rpcRequestQueue.add(request);
    }

    public void addRPCResponse(RPCResponse response) {
        rpcResponseQueue.add(response);
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    // 游戏线程主循环中需要处理RPC请求和响应
    public void processRPC() {
        // 处理RPC响应
        processRPCResponses();

        // 处理RPC请求
        processRPCRequests();
    }

    private void processRPCResponses() {
        RPCResponse response;
        while ((response = rpcResponseQueue.poll()) != null) {
            // 处理RPCResponse应答
            processSingleRPCResponse(response);
        }
    }

    private void processSingleRPCResponse(RPCResponse response) {
        // 处理RPCResponse应答
        CompletableFuture<Object> future = callbackMap.remove(response.getRequestId());
        if (future != null) {
            if (response.getError() != null) {
                future.completeExceptionally(new RuntimeException(response.getError()));
            } else {
                future.complete(response.getData());
            }
        } else {
            logger.error("RPCResponse not found for requestId: {}", response.getRequestId());
        }
    }

    private void processRPCRequests() {
        RPCRequest request;
        while ((request = rpcRequestQueue.poll()) != null) {
            // 处理单个RPC请求
            processSingleRPCRequest(request);
        }
    }

    private void processSingleRPCRequest(RPCRequest request) {
        // 根据request获取目标服务并调用方法
        // 这里需要根据实际架构实现
        logger.info("Processing RPC request: {}", request);

        invokeTargetMethod(request);
    }

    private Object invokeTargetMethod(RPCRequest request) {
        // 从gameServices获取服务对象
        String gameServiceName = request.getInvocation().getToPoint().getGameServiceName();
        GameServiceBase gameServiceBase = gameServices.get(gameServiceName);
        if (gameServiceBase == null) {
            throw new RuntimeException("GameService not found: " + gameServiceName);
        }

        // 获取目标方法名
        String methodName = request.getInvocation().getMethodName();
        Object[] parameters = request.getInvocation().getParameters().toArray();

        // 反射调用方法
        try {
            Object result = MethodUtils.invokeMethod(gameServiceBase, methodName, parameters);

            // 根据方法返回值类型构造RPCResponse
            if (result instanceof CompletableFuture) {
                // 先获取结果，然后构造RPCResponse
                CompletableFuture<?> future = (CompletableFuture<?>) result;
                RPCResponse response = new RPCResponse(request.getRequestId(), request.getInvocation().getFromPoint(), future.get(), null);
                handleResponse(response);
            } else {
                // 返回值类型错误
                RPCResponse response = new RPCResponse(request.getRequestId(), request.getInvocation().getFromPoint(), null, "Invalid return type: " + result.getClass().getName());
                handleResponse(response);
            }
        } catch (InvocationTargetException e) {
            logger.error("Failed to invoke method: {}", request.getInvocation().getMethodName(), e);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            logger.error("Method not found: {}", request.getInvocation().getMethodName(), e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.error("Access denied to method: {}", request.getInvocation().getMethodName(), e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            logger.error("Error executing method: {}", request.getInvocation().getMethodName(), e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            logger.error("Interrupted while executing method: {}", request.getInvocation().getMethodName(), e);
            throw new RuntimeException(e);
        }

        return null;
    }

    private void handleResponse(RPCResponse response) {
        // 序列化response为json

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonResponse = objectMapper.writeValueAsString(response);

            // 发送
            parseRPCResponse(jsonResponse);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize RPC response = {}", response);
            throw new RuntimeException(e);
        }
    }

    private void parseRPCResponse(String responseJson) {
        // 反序列化 response
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            RPCResponse response = objectMapper.readValue(responseJson, RPCResponse.class);
            logger.debug("Deserialized response : {}", response);
            dispatchResponse(response);
        } catch (IOException e) {
            logger.error("Failed to deserialize response: {}", responseJson, e);
        }

    }

    private void dispatchResponse(RPCResponse response) {
        // 获取线程名
        String fromGameThreadName = response.getFromPoint().getGameThreadName();
        GameThread fromGameThread = GameProcess.getGameThread(fromGameThreadName);

        if (fromGameThread == null) {
            logger.warn("GameThread not found: {}", fromGameThreadName);
            return;
        }
        fromGameThread.addRPCResponse( response);
    }

    // 添加回调到Map中
    public void addCallback(RPCRequest request, CompletableFuture<Object> future) {
        callbackMap.put(request.getRequestId(), future);

        callBackTimeoutQueue.delay(CALLBACK_TIMEOUT, (timerId, context) -> {
            logger.error("RPC callback timeout: {}", request);
            CompletableFuture<Object> callback = callbackMap.remove(request.getRequestId());
            if (callback != null && !callback.isDone()) {
                callback.completeExceptionally(new RuntimeException("RPC call timeout: " + request));
            }
        }, new Param());
    }

    // 移除回调
    public void removeCallback(String requestId) {
        callbackMap.remove(requestId);
    }

    // 获取回调
    public CompletableFuture<Object> getCallback(String requestId) {
        return callbackMap.remove(requestId);
    }
}