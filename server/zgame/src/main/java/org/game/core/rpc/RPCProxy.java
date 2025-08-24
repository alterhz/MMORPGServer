package org.game.core.rpc;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCProxy {
    
    /**
     * 启动类型枚举
     */
    enum StartupType {
        /**
         * 默认启动
         */
        DEFAULT,
        
        /**
         * 手动启动
         */
        MANUAL
    }
    
    /**
     * 启动类型
     * @return 启动类型
     */
    StartupType startupType() default StartupType.DEFAULT;
}