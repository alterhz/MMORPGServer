package org.game.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册管理器
 * <p>负责管理所有游戏服务的注册信息，包括服务名称与线程名称的映射关系</p>
 * <p>工作流程：
 * <ol>
 *   <li>服务启动时调用registerService方法注册服务</li>
 *   <li>RPC调用时通过getServiceGameThreadName方法获取服务所在的游戏线程</li>
 * </ol>
 * </p>
 *
 * @author Lingma
 * @date 2025-08-01
 */
public class ServiceRegistryManager {
    
    private static final Logger logger = LogManager.getLogger(ServiceRegistryManager.class);
    
    /**
     * 服务注册表: 服务名称 -> 线程名称
     */
    private static final ConcurrentHashMap<String, String> serviceRegistry = new ConcurrentHashMap<>();
    
    /**
     * 注册服务
     * <p>将服务名称与当前线程名称关联存储</p>
     * 
     * @param serviceName 服务名称
     * @param gameThreadName 游戏线程名称
     * @return 如果服务已存在则返回false，否则返回true
     */
    public static boolean registerService(String serviceName, String gameThreadName) {
        String existingThreadName = serviceRegistry.putIfAbsent(serviceName, gameThreadName);
        if (existingThreadName != null) {
            logger.warn("服务已注册: {} 注册线程: {} 当前线程: {}", serviceName, existingThreadName, gameThreadName);
            return false;
        }
        logger.info("服务注册成功: {} 线程: {}", serviceName, gameThreadName);
        return true;
    }
    
    /**
     * 获取服务所在的游戏线程名称
     * 
     * @param serviceName 服务名称
     * @return 游戏线程名称，如果服务未注册则返回null
     */
    public static String getServiceGameThreadName(String serviceName) {
        return serviceRegistry.get(serviceName);
    }
    
    /**
     * 清空服务注册表
     * <p>仅供测试使用</p>
     */
    public static void clearRegistry() {
        serviceRegistry.clear();
    }
}