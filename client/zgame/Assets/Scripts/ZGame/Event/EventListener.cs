using System;

namespace ZGame
{
    /// <summary>
    /// 协议监听器注解特性，用于标识处理协议响应的方法
    /// </summary>
    [AttributeUsage(AttributeTargets.Method, AllowMultiple = false)]
    public class EventListener : Attribute
    {

    }
}