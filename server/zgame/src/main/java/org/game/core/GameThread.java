package org.game.core;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.net.ConnThread;
import org.game.core.rpc.RPCRequest;
import org.game.core.rpc.RPCResponse;
import org.game.core.utils.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
     * RPC耗时打印周期
     */
    public static final int PERIOD_MS = 30 * 1000;

    /**
     * 游戏服务列表:服务名称为key，服务实例为value
     */
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
    private final ConcurrentHashMap<String, TimerFuture> callbackMap = new ConcurrentHashMap<>();

    // RPC调用耗时：方法名为key，value:耗时, 总调用次数
    private final Map<String, Pair<Long, Long>> rpcCost = new HashMap<>();


    static class TimerFuture {
        private final long timerId;
        private final CompletableFuture<Object> future;

        public TimerFuture(long timerId, CompletableFuture<Object> future) {
            this.timerId = timerId;
            this.future = future;
        }

        public long getTimerId() {
            return timerId;
        }

        public CompletableFuture<Object> getFuture() {
            return future;
        }
    }

    // 超时时间（毫秒）
    private static final long CALLBACK_TIMEOUT = 30000;

    /**
     * 定时器队列
     */
    private final TimerQueue timerQueue = new TimerQueue();

    public GameThread(String name) {
        setName(name);
        this.setDaemon(true); // 设置为守护线程
    }

    @Override
    public synchronized void start() {
        super.start();
        GameProcess.addGameThread(this);

        timerQueue.createTimer(PERIOD_MS, PERIOD_MS, (timerId, context) -> {
            printRpcCost();
        }, new Param());
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
            } catch (RuntimeException e) {
                logger.error("GameThread运行异常", e);
            } catch (InterruptedException e) {
                logger.error("GameThread interrupted", e);
            }
        }
    }

    // 异步执行任务
    public void runTask(Runnable task) {
        taskQueue.offer(task);
    }

    // 执行任务队列中的任务
    private void executeTasks() {
        Runnable task;
        while ((task = taskQueue.poll()) != null) {
            task.run();
        }
    }

    // 心跳方法
    private void pulse(long now) {
        // 执行所有GameService的心跳
        for (GameServiceBase service : gameServices.values()) {
            service.pulse(now);
        }

        timerQueue.update(now);
    }

    // 添加GameService
    public void addGameService(GameServiceBase service) {
        if (gameServices.containsKey(service.getName())) {
            logger.error("Service: {} 已存在", service.getName());
        }
        gameServices.put(service.getName(), service);
        service.bindGameThread(this);
        if (logger.isDebugEnabled()) {
            // service绑定线程
            logger.debug("Service: {} 绑定线程: {}", service.getName(), service.getGameThread().getName());
        }
    }

    public void removeGameService(GameServiceBase service) {
        gameServices.remove(service.getName());
        service.destroy();
        if (logger.isDebugEnabled()) {
            // service解绑线程
            logger.debug("Service: {} 解绑线程: {}", service.getName(), service.getGameThread().getName());
        }
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
        logger.info("GameThread: {} 销毁", getName());
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
        TimerFuture timerFuture = callbackMap.remove(response.getRequestId());
        if (timerFuture != null) {
            if (response.getError() != null) {
                timerFuture.getFuture().completeExceptionally(new RuntimeException(response.getError()));
            } else {
                // 支持void类型返回
                if (response.getData() == null) {
                    // void
                    timerFuture.getFuture().complete(null);
                } else {
                    timerFuture.getFuture().complete(response.getData());
                }
            }
            timerQueue.cancelTimer(timerFuture.getTimerId());
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
//        logger.info("Processing RPC request: {}", request);

        invokeTargetMethod(request);
    }

    private Object invokeTargetMethod(RPCRequest request) {
        long start = System.nanoTime();
        // 从gameServices获取服务对象
        String gameServiceName = request.getInvocation().getToPoint().getGameServiceName();
        GameServiceBase gameServiceBase = gameServices.get(gameServiceName);
        if (gameServiceBase == null) {
            // 判断当前位于ConnThread线程
            logger.error("GameService not found: {}", request);
            if (!(GameThread.getCurrentGameThread() instanceof ConnThread)) {
                throw new RuntimeException("GameService not found: " + gameServiceName + ", request=" + request);
            } else {
                // ConnThread线程，直接返回null
                return null;
            }
        }

        // 获取目标方法名
        String methodName = request.getInvocation().getMethodName();
        Object[] parameters = request.getInvocation().getTypedParameters().toArray();

        // 反射调用方法
        try {
            // 无需优化：与获取方法，再调用耗时一致。
            Object result = MethodUtils.invokeMethod(gameServiceBase, methodName, parameters);

            // 根据方法返回值类型构造RPCResponse
            if (result instanceof CompletableFuture) {
                // 先获取结果，然后构造RPCResponse
                CompletableFuture<?> future = (CompletableFuture<?>) result;
                future.whenComplete((o, throwable) -> {
                    RPCResponse response;
                    if (throwable != null) {
                        // 方法执行异常，构造错误响应
                        response = new RPCResponse(request.getRequestId(), request.getInvocation().getFromPoint(), null, throwable.getMessage());
                    } else {
                        // 方法执行成功，构造成功响应
                        response = new RPCResponse(request.getRequestId(), request.getInvocation().getFromPoint(), o, null);
                    }
                    handleResponse(response);
                });
            } else if (result == null) {
                // 返回值为void或null的情况，不做处理
                RPCResponse response = new RPCResponse(request.getRequestId(), request.getInvocation().getFromPoint(), null, null);
                handleResponse(response);
            } else {
                // 返回值类型错误
                RPCResponse response = new RPCResponse(request.getRequestId(), request.getInvocation().getFromPoint(), null, "Invalid return type: " + result.getClass().getName());
                handleResponse(response);
            }
        } catch (InvocationTargetException e) {
            logger.error("Failed to invoke method: {}", request.getInvocation().getMethodName(), e);
        } catch (IllegalAccessException e) {
            logger.error("Access denied to method: {}", request.getInvocation().getMethodName(), e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 统计结束
        long end = System.nanoTime();
        // 耗时记录到rpcCost中
        String serviceMethodName = gameServiceName + "#" + methodName;
        Pair<Long, Long> pair = rpcCost.get(serviceMethodName);
        if (pair == null) {
            rpcCost.put(serviceMethodName, ImmutablePair.of(end - start, 1L));
        } else {
            rpcCost.put(serviceMethodName, ImmutablePair.of(pair.getLeft() + (end - start), pair.getRight() + 1));
        }

        return null;
    }

    public void printRpcCost() {
        // 打印RPC 函数名称，总耗时，总次数，每万次耗时
        for (Map.Entry<String, Pair<Long, Long>> entry : rpcCost.entrySet()) {
            Pair<Long, Long> pair = entry.getValue();
            long count = pair.getRight();
            Long cost = pair.getLeft();
            logger.info("RPC Cost: method={} - totalCost={}ms - totalCount={} averageCost={}ms", entry.getKey(), String.format("%.3f", cost / 1000000.0f), count, String.format("%.3f", cost / count / 1000000.0f));
        }
        rpcCost.clear();
    }

    private void handleResponse(RPCResponse response) {
//        String jsonResponse = JsonUtils.tryEncode(response);
//        if (jsonResponse == null) {
//            return;
//        }
//        logger.debug("Serialized response = {}", jsonResponse);
//        parseRPCResponse(jsonResponse);

        dispatchResponse(response);
    }

    private void parseRPCResponse(String responseJson) {
        RPCResponse rpcResponse = JsonUtils.tryDecode(responseJson, RPCResponse.class);
        if (rpcResponse == null) {
            return;
        }
        logger.debug("Deserialized response : {}", rpcResponse);
        dispatchResponse(rpcResponse);
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
        long timerId = timerQueue.delay(CALLBACK_TIMEOUT, (id, context) -> {
            logger.error("RPC timerFuture timeout: {}", request);
            TimerFuture timerFuture = callbackMap.remove(request.getRequestId());
            if (timerFuture != null && !timerFuture.getFuture().isDone()) {
                timerFuture.getFuture().completeExceptionally(new RuntimeException("RPC call timeout: " + request));
            }
        }, new Param());

        callbackMap.put(request.getRequestId(), new TimerFuture(timerId, future));
    }

    // 移除回调
    public void removeCallback(String requestId) {
        callbackMap.remove(requestId);
    }

}