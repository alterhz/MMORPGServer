# RPC使用文档

## 1、定义服务接口IHumanGlobalService，位于包org.game.rpc下：
```java
@RPCProxy()
public interface IHumanGlobalService {
    /**
     * 获取在线人数
     **/
    CompletableFuture<Integer> getHumanOnlineCount(int minLevel);

    /**
     * 热修复
     */
    void hotfix();
}
```

## 2、实现服务类StageGlobalService，位于包org.game.service下：
```java
public class HumanGlobalService extends GameServiceBase implements IHumanGlobalService {

    public static final Logger logger = LogManager.getLogger(HumanGlobalService.class);
    
    public HumanGlobalService(String name) {
        super(name);
    }

    // 实现基本方法
    @Override
    public void init() {
        // 初始化逻辑
        logger.info("HumanGlobalService 初始化");
    }

    @Override
    public void startup() {
        logger.info("HumanGlobalService 启动");

    }

    @Override
    public void pulse(long now) {

    }

    @Override
    public void destroy() {
        // 销毁逻辑
        logger.info("HumanGlobalService 销毁");
    }

    @Override
    public CompletableFuture<Integer> getHumanOnlineCount(int minLevel) {
        return CompletableFuture.completedFuture(onlineCount.get() + 101);
    }

    @Override
    public void hotfix() {
        logger.info("HumanGlobalService 热修复");
    }
}
```

## 3、创建服务代理并调用方法：

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

这个示例展示了如何获取远程服务代理并调用其方法，以及如何处理异步返回的结果。