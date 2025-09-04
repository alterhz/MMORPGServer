# GlobalRPC 开发指南

GlobalRPC 是一种全局远程过程调用（RPC）框架，用于在不同线程间调用全局服务。与 HumanRPC 不同，GlobalRPC 不依赖于特定的角色对象，而是提供全局可用的服务。

## 1. GlobalRPC 基本概念

GlobalRPC 主要由以下几个组件构成：

- **GameServiceBase**: 全局服务基类，所有全局服务都继承自此类
- **RPCProxy**: 注解，标记接口为 GlobalRPC 接口
- **IXXXService**: 全局服务接口，定义全局相关的方法
- **XXXService**: 全局服务实现类，实现具体业务逻辑
- **ReferenceFactory**: 用于获取服务代理实例的工厂类

## 2. 开发步骤

### 2.1 定义服务接口

首先创建一个接口并使用 `@RPCProxy` 注解标记：

```java
package org.game.global.rpc;

import org.game.core.Param;
import org.game.core.rpc.RPCProxy;

import java.util.concurrent.CompletableFuture;

@RPCProxy()
public interface IHumanGlobalService {
    /**
     * 获取在线人数
     **/
    CompletableFuture<Integer> getHumanOnlineCount(int minLevel);

    /**
     * 测试
     */
    void test();

    /**
     * 热修复
     */
    void hotfix(Param param);
}
```

注意：
- 接口必须使用 `@RPCProxy` 注解
- 方法可以返回 `CompletableFuture<T>` 类型以支持异步调用
- 方法也可以是 void 类型，用于不需要返回值的调用

### 2.2 创建服务实现类

创建服务实现类，继承 [GameServiceBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/GameServiceBase.java#L8-L123) 并实现对应的接口：

```java
package org.game.global.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.global.rpc.IHumanGlobalService;
import java.util.concurrent.CompletableFuture;

public class HumanGlobalService extends GameServiceBase implements IHumanGlobalService {

    public static final Logger logger = LogManager.getLogger(HumanGlobalService.class);

    public HumanGlobalService(String name) {
        super(name);
    }

    @Override
    public void init() {
        logger.info("HumanGlobalService 初始化");
    }

    @Override
    public void startup() {
        logger.info("HumanGlobalService 启动");
    }

    @Override
    public void pulse(long now) {
        // 定时任务逻辑
    }

    @Override
    public void destroy() {
        logger.info("HumanGlobalService 销毁");
    }

    @Override
    public CompletableFuture<Integer> getHumanOnlineCount(int minLevel) {
        // 简化示例，实际应根据参数返回真实在线人数
        return CompletableFuture.completedFuture(101);
    }

    @Override
    public void test() {
        // 测试方法实现
        logger.info("执行测试方法");
    }

    @Override
    public void hotfix(Param param) {
        logger.info("HumanGlobalService 热修复: param={}", param);
    }
}
```

注意：
- 服务类必须继承 [GameServiceBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/GameServiceBase.java#L8-L123)
- 构造函数必须接受 name 参数并调用父类构造函数
- 实现接口中定义的所有方法
- 对于返回 `CompletableFuture<T>` 的方法，可以使用 `CompletableFuture.completedFuture()` 返回结果

### 2.3 在客户端调用服务

在需要调用 GlobalRPC 服务的地方，使用 [ReferenceFactory.getProxy()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/ReferenceFactory.java#L23-L32) 方法获取代理对象：

```java
// 获取服务代理
IHumanGlobalService humanGlobalService = ReferenceFactory.getProxy(IHumanGlobalService.class);

// 异步调用获取在线人数
final CompletableFuture<Integer> future = humanGlobalService.getHumanOnlineCount(1);

future.whenComplete((count, throwable) -> {
    if (throwable != null) {
        logger.error("获取在线人数失败", throwable);
    } else {
        // 异步处理结果
        logger.info("在线人数 = {}", count);
    }
});
```

对于 void 类型的方法，可以直接调用：

```java
// 获取服务代理
IHumanGlobalService humanGlobalService = ReferenceFactory.getProxy(IHumanGlobalService.class);

// 调用void方法
humanGlobalService.test();
```

注意：
- 使用 [ReferenceFactory.getProxy()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/ReferenceFactory.java#L23-L32) 方法获取代理对象
- 参数是服务接口的 Class 对象
- 通过 `whenComplete`、`thenAccept`、`thenApply` 等方法处理异步回调结果
- void 方法可以直接调用，无需处理返回值

## 3. 工作原理

1. 当调用 [ReferenceFactory.getProxy()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/ReferenceFactory.java#L23-L32) 时，会创建一个基于 [RemoteRPCInvoker](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/RemoteRPCInvoker.java#L24-L185) 的动态代理对象
2. 调用代理对象的方法时，会通过 [RemoteRPCInvoker](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/RemoteRPCInvoker.java#L24-L185) 拦截方法调用
3. [RemoteRPCInvoker](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/RemoteRPCInvoker.java#L24-L185) 将方法调用封装成 RPC 请求，并发送到目标服务所在的线程
4. 目标线程接收到请求后，查找对应的服务实例并执行方法
5. 执行结果通过 RPC 响应返回给调用方

## 4. 注意事项

1. GlobalRPC 方法可以返回 `CompletableFuture<T>` 类型（异步）或 void 类型（无返回值）
2. 服务实现类必须继承 [GameServiceBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/GameServiceBase.java#L8-L123) 并实现相应的接口
3. 服务类必须有接受 name 参数的构造函数
4. GlobalRPC 是跨线程调用，需要通过回调处理异步结果
5. GlobalRPC 服务是全局服务，不依赖于特定的角色对象