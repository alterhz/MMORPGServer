# zgame项目RPC调用流程文档

## 1. 概述

zgame项目中的RPC（Remote Procedure Call）机制用于实现游戏服务间的跨线程调用。该机制通过Java动态代理、反射和线程间通信实现，允许一个服务线程调用另一个服务线程中的方法。

## 2. 核心组件

### 2.1 RPC数据结构

- RPCRequest: RPC请求数据结构，包含请求ID和调用信息
- RPCResponse: RPC响应数据结构，包含请求ID、来源信息、返回数据和错误信息
- RpcInvocation: RPC调用信息，包含来源点、目标点、方法名、参数和参数类型
- FromPoint: 调用来源信息，包含进程名和线程名
- ToPoint: 调用目标信息，包含进程名、线程名和服务名

### 2.2 RPC核心处理类

- RemoteRPCInvoker: RPC调用处理器，实现InvocationHandler接口，负责拦截方法调用并构建RPC请求
- ReferenceFactory: RPC代理工厂，用于创建服务接口的代理实例

### 2.3 线程和服务管理

- GameThread: 游戏线程，处理任务队列、心跳和RPC请求/响应
- GameProcess: 游戏进程，管理所有游戏线程
- GameServiceBase: 服务基类，所有服务都需要继承此类

## 3. RPC调用流程

### 3.1 服务定义和注册

1. 定义服务接口并添加@RPCProxy注解
2. 实现服务接口并继承GameServiceBase基类
3. 服务启动时会自动注册到服务注册表中

### 3.2 代理创建

1. 使用ReferenceFactory.getProxy()方法创建服务接口的代理实例
2. 代理实例使用RemoteRPCInvoker作为调用处理器

### 3.3 调用过程

1. 当调用代理实例的方法时，RemoteRPCInvoker.invoke()方法会被触发
2. 构建调用信息，包括：
    - 获取当前线程信息作为来源点(FromPoint)
    - 根据服务接口确定目标点(ToPoint)
    - 封装方法名、参数和参数类型
3. 创建RPCRequest对象并生成唯一请求ID
4. 创建CompletableFuture用于接收回调结果，并将其存储在回调映射中
5. 将请求序列化为JSON并发送

### 3.4 请求分发

1. dispatchRPCRequest()方法根据目标线程名查找目标GameThread
2. 如果未指定目标线程，则根据服务名查找对应的线程
3. 将RPCRequest添加到目标线程的RPC请求队列中

### 3.5 请求处理

1. 目标GameThread在主循环中处理RPC请求
2. processSingleRPCRequest()方法根据请求信息找到目标服务
3. 使用反射调用目标方法
4. 将结果封装为RPCResponse并发送回来源线程

### 3.6 响应处理

1. 来源GameThread接收到RPCResponse后，根据请求ID找到对应的CompletableFuture
2. 将结果设置到CompletableFuture中，完成整个RPC调用过程

## 4. 超时处理

系统为每个RPC调用设置了30秒的超时时间。如果在超时时间内未收到响应，系统会自动完成对应的CompletableFuture并抛出超时异常。

