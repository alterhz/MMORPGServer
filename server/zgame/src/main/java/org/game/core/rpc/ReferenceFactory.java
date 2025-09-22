package org.game.core.rpc;

import java.lang.reflect.Proxy;

public class ReferenceFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(RPCProxy.class)) {
            throw new IllegalArgumentException("Interface must be annotated with @RPCProxy");
        }

        // 直接创建代理实例，不使用缓存
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new RemoteRPCInvoker()
        );
    }

    // 添加新方法，支持调用带ToPoint参数的RemoteRPCInvoker构造函数
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz, ToPoint toPoint) {
        if (!clazz.isAnnotationPresent(RPCProxy.class)) {
            throw new IllegalArgumentException("Interface must be annotated with @RPCProxy");
        }

        // 直接创建代理实例，不使用缓存
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new RemoteRPCInvoker(toPoint)
        );
    }

    public static <T> T getPlayerProxy(Class<T> clazz, long playerId) {
        if (!clazz.isAnnotationPresent(HumanRPCProxy.class)) {
            throw new IllegalArgumentException("Interface must be annotated with @HumanRPCProxy");
        }
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new HumanRPCInvoker(playerId)
        );
    }
}
