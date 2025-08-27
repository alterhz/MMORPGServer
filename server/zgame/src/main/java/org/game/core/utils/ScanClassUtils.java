package org.game.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanClassUtils {

    private static final Logger logger = LogManager.getLogger(ScanClassUtils.class);

    // 扫描所有的类
    public static Set<Class<?>> scanAllClasses() {
        Set<Class<?>> serviceClasses = new HashSet<>();

        try {
            // 获取org.game包的URL路径
            String packageName = "org.game";
            String packagePath = packageName.replace(".", "/");
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);

            // 遍历所有可能的包路径
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    // 从文件系统加载类
                    File packageDir = new File(resource.toURI());
                    collectClassesFromDirectory(packageDir, packageName, serviceClasses);
                } else if ("jar".equals(resource.getProtocol())) {
                    // 从jar包加载类
                    JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
                    jarConnection.setUseCaches(false); // 避免缓存导致类加载问题
                    JarFile jarFile = jarConnection.getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().startsWith(packagePath) && entry.getName().endsWith(".class")) {
                            String className = entry.getName()
                                    .replace("/", ".")
                                    .substring(0, entry.getName().length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);
                                serviceClasses.add(clazz);
                            } catch (Throwable e) {
                                logger.error("加载类失败: {}", className, e);
                            }
                        }
                    }
                }
                // 可以添加其他协议的处理（如jar包等）
            }
        } catch (Exception e) {
            logger.error("扫描服务类失败", e);
            throw new RuntimeException("扫描服务类失败", e);
        }

        return serviceClasses;
    }

    // 递归收集指定目录下的所有类
    public static void collectClassesFromDirectory(File directory, String packageName, Set<Class<?>> classes) {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归处理子目录
                    collectClassesFromDirectory(file, packageName + "." + file.getName(), classes);
                } else if (file.getName().endsWith(".class")) {
                    // 加载类
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (Throwable e) {
                        // 忽略加载失败的类
                        logger.error("加载类失败: {}", className, e);
                    }
                }
            }
        }
    }
}