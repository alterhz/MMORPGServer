# HumanDB 开发文档

## 1. 概述

HumanDB 是游戏服务端框架中用于处理角色数据持久化的模块。它提供了一种基于注解的机制，可以方便地将角色相关的数据从 MongoDB 数据库中加载到内存中，并支持数据的初始化和更新。

## 2. 核心组件

### 2.1 HumanDBManager
[HumanDBManager](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanDBManager.java#L20-L128) 是核心管理类，负责扫描所有带有 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解的方法，并在角色登录时自动加载相关数据。

### 2.2 @HumanLoader 注解
[@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 是一个方法级注解，用于标记加载特定数据集合的方法。该注解需要指定一个带有 [@Entity](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/Entity.java#L12-L14) 注解的实体类。

### 2.3 @Entity 注解
[@Entity](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/Entity.java#L12-L14) 是一个类级注解，用于标记 MongoDB 数据库实体类，需要指定集合名称。

## 3. 开发步骤

### 3.1 定义数据实体类

首先需要创建一个数据实体类，并使用 [@Entity](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/Entity.java#L12-L14) 注解标记：

```java
@Entity(collectionName = "humanInfos")
public class HumanInfoDB {
    private ObjectId id;
    private String humanId;
    private String info;
    
    // getter 和 setter 方法
}
```

### 3.2 在HMod类中实现包含@HumanLoader注解的函数

在继承自 [HModBase](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/human/HModBase.java#L3-L22) 的模块类中，实现带有 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解的函数来加载特定数据集合：

```java
public class HModInfo extends HModBase {
    public HModInfo(HumanObject humanObj) {
        super(humanObj);
    }

    // 使用@HumanLoader注解标记加载方法，指定对应的实体类
    @HumanLoader(entity = HumanInfoDB.class)
    public void loadHumanInfo(List<HumanInfoDB> playerInfoDBS) {
        // 处理加载的数据
        logger.info("加载HumanInfoDB：{}", playerInfoDBS);
        
        // 可以在这里处理数据，例如：
        // 1. 如果没有数据，则插入默认数据
        if (playerInfoDBS.isEmpty()) {
            HumanInfoDB playerInfoDB = new HumanInfoDB();
            playerInfoDB.setHumanId(humanObj.getId());
            playerInfoDB.setInfo("这是测试数据");
            
            MongoDBAsyncClient.insertOne(playerInfoDB);
        }
        // 2. 将数据保存到模块内部供后续使用
        // 3. 进行数据验证或其他处理
    }
}
```

带有 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解的函数需要遵循以下规范：
1. 必须是非静态方法
2. 方法参数必须是 `List<T>` 类型，其中 T 是对应的实体类
3. 方法将在角色登录时自动调用，传入从数据库查询到的数据列表
4. 如果数据库中没有匹配的数据，将传入一个空列表

### 3.3 数据加载流程

1. 服务启动时，[HumanDBManager.init()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanDBManager.java#L25-L27) 方法会扫描所有带有 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解的方法并缓存起来
2. 当角色登录时，[HumanDBManager.loadHumanModDB()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanDBManager.java#L33-L64) 方法会遍历所有缓存的方法
3. 对于每个方法，会从 MongoDB 中查询对应的数据集合
4. 查询结果会通过反射调用相应的方法，将数据传递给 HMod 类进行处理

## 4. 工作原理

[HumanDBManager](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanDBManager.java#L20-L128) 使用 Java 反射机制和注解处理来实现自动化的数据加载：

1. 通过 [HModScanner.getHModClasses()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/human/HModScanner.java#L21-L23) 获取所有 HMod 类
2. 遍历每个类的方法，查找带有 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解的方法
3. 提取 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解中的实体类信息
4. 验证实体类是否带有 [@Entity](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/Entity.java#L12-L14) 注解
5. 在角色登录时，使用 MongoDB 异步客户端查询数据
6. 通过 [QuerySubscriber](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/QuerySubscriber.java#L11-L54) 处理查询结果并调用相应的方法

## 5. 注意事项

1. 带有 [@HumanLoader](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/HumanLoader.java#L10-L12) 注解的方法必须是非静态方法
2. 方法参数必须是 `List<T>` 类型，其中 T 是对应的实体类
3. 实体类必须使用 [@Entity](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/core/db/Entity.java#L12-L14) 注解标记
4. 数据加载是异步进行的，需要确保在数据加载完成后再执行依赖这些数据的操作
5. 可以通过重写 [HumanObject.onLoadingComplete()](file:///D:/MyZiegler/ZRepo/github/MMORPGServer/server/zgame/src/main/java/org/game/human/HumanObject.java#L94-L96) 方法来处理数据加载完成后的逻辑