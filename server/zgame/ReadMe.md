# zgame 服务端框架

zgame 是一个基于 Java 的游戏服务端框架，用于构建网络化游戏逻辑。该项目采用多线程架构设计，提供了高性能的 RPC 调用机制，支持服务间的跨线程通信。

## 主要特性

- 基于 Netty 的高性能网络通信
- 多线程处理游戏逻辑
- 自动服务发现与注册
- 异步 RPC 调用机制
- YAML 配置文件支持
- 日志系统集成

## 文档

详细文档请参考以下链接：

- [RPC 调用流程文档](docs/RPC.md) - 详细介绍项目中的 RPC 调用机制和实现原理
- [HumanRPC 框架开发指南](docs/HumanRPC.md) - 详细介绍 HumanRPC 框架的使用方法
- [GlobalRPC 框架开发指南](docs/GlobalRPC.md) - 详细介绍 GlobalRPC 框架的使用方法
- [HumanDB 数据库模块](docs/HumanDB.md) - 详细介绍 HumanDB 数据库模块的使用方法
- [序列化机制](docs/Serialize.md) - 介绍系统中的序列化机制

## 快速开始

### 环境要求

- Java 11+
- Gradle

### 构建项目

```bash
gradle build
```

### 运行项目

```bash
gradle run
```