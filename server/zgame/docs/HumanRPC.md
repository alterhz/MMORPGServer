# HumanRPC 开发指南

HumanRPC 是一个基于角色对象（HumanObject）的远程过程调用（RPC）框架，允许在不同线程间调用与特定角色相关的服务。

## 1. HumanRPC 基本概念

HumanRPC 主要由以下几个组件构成：

- **HumanObject**: 角色对象，包含角色的所有数据和行为
- **HumanServiceBase**: 角色服务基类，所有角色相关服务都继承自此类
- **HumanRPCProxy**: 注解，标记接口为 HumanRPC 接口
- **IHumanXXXService**: 角色服务接口，定义角色相关的方法
- **HumanXXXService**: 角色服务实现类，实现具体业务逻辑

## 2. 开发步骤

### 2.1 定义服务接口

首先创建一个接口并使用 `@HumanRPCProxy` 注解标记：

```java
package org.game.player.rpc;

import org.game.core.rpc.HumanRPCProxy;
import org.game.player.module.MyStruct;

import java.util.concurrent.CompletableFuture;

@HumanRPCProxy
public interface IHumanInfoService {
    CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct);
}
```

注意：
- 接口必须使用 `@HumanRPCProxy` 注解
- 所有方法必须返回 `CompletableFuture<T>` 类型，支持异步调用
- 方法参数可以是基本类型、自定义对象等

### 2.2 创建服务实现类

创建服务实现类，继承 [HumanServiceBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/HumanServiceBase.java#L8-L16) 并实现对应的接口：

```java
package org.game.player.service;

import org.game.core.rpc.HumanServiceBase;
import org.game.player.PlayerObject;
import org.game.player.module.MyStruct;
import org.game.player.rpc.IPlayerInfoService;

import java.util.concurrent.CompletableFuture;

public class HumanInfoService extends HumanServiceBase implements IHumanInfoService {
    public HumanInfoService(HumanObject humanObj) {
        super(humanObj);
    }

    @Override
    public CompletableFuture<String> getInfo(int a, String b, MyStruct myStruct) {
        String id = humanObj.getId();
        return CompletableFuture.completedFuture("HumanInfoService" + id);
    }
}
```

注意：
- 服务类必须继承 [HumanServiceBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/HumanServiceBase.java#L8-L16)
- 构造函数必须接受 [HumanObject](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/human/HumanObject.java#L24-L181) 参数并调用父类构造函数
- 实现接口中定义的所有方法，返回 `CompletableFuture<T>` 类型

### 2.3 在客户端调用服务

在需要调用 HumanRPC 服务的地方，使用 [ReferenceFactory.getHumanProxy()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/ReferenceFactory.java#L34-L44) 方法获取代理对象：

```java
// 获取 HumanRPC 代理
IHumanInfoService humanInfoService = ReferenceFactory.getHumanProxy(IHumanInfoService.class, humanId);

// 创建参数对象
MyStruct myStruct = new MyStruct();
myStruct.setId(1);
myStruct.setName("张三");
myStruct.setSex(true);
myStruct.setDesc("测试");

// 调用服务方法
humanInfoService.getInfo(33, "dfsd", myStruct).whenComplete((humanInfo, throwable) -> {
    if (throwable != null) {
        logger.error("获取角色信息失败: ", throwable);
    } else {
        logger.info("获取角色信息成功: {}", humanInfo);
    }
});
```

注意：
- 使用 [ReferenceFactory.getHumanProxy()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/ReferenceFactory.java#L34-L44) 方法获取代理对象
- 第一个参数是服务接口的 Class 对象
- 第二个参数是角色的 ID
- 通过 `thenAccept`、`thenApply` 等方法处理异步回调结果

## 3. 工作原理

1. 当调用 [ReferenceFactory.getHumanProxy()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/ReferenceFactory.java#L34-L44) 时，会创建一个基于 [HumanRPCInvoker](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/HumanRPCInvoker.java#L22-L170) 的动态代理对象
2. 调用代理对象的方法时，会通过 [HumanRPCInvoker](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/HumanRPCInvoker.java#L22-L170) 拦截方法调用
3. [HumanRPCInvoker](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/HumanRPCInvoker.java#L22-L170) 将方法调用封装成 RPC 请求，并发送到目标角色所在的线程
4. 目标线程接收到请求后，查找对应的角色服务实例并执行方法
5. 执行结果通过 RPC 响应返回给调用方

## 4. 注意事项

1. 所有 HumanRPC 方法必须返回 `CompletableFuture<T>` 类型
2. 服务实现类必须继承 [HumanServiceBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/rpc/HumanServiceBase.java#L8-L16) 并实现相应的接口
3. 服务类必须有接受 [HumanObject](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/human/HumanObject.java#L24-L181) 参数的构造函数
4. 调用 HumanRPC 时需要提供正确的角色 ID
5. HumanRPC 是异步调用，需要通过回调处理结果