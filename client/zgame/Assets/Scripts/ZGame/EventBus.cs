using System;
using System.Collections.Generic;

/// <summary>
/// 全局事件总线：解耦UI与逻辑层
/// </summary>
public static class EventBus
{
    // 存储事件：Key=事件类型，Value=该事件的所有回调
    private static readonly Dictionary<Type, Delegate> _eventDict = new();

    /// <summary>
    /// 订阅事件
    /// </summary>
    public static void Subscribe<T>(Action<T> callback) where T : class
    {
        if (!_eventDict.ContainsKey(typeof(T)))
            _eventDict[typeof(T)] = null;
        
        _eventDict[typeof(T)] = (Action<T>)_eventDict[typeof(T)] + callback;
    }

    /// <summary>
    /// 取消订阅事件（防止内存泄漏）
    /// </summary>
    public static void Unsubscribe<T>(Action<T> callback) where T : class
    {
        if (_eventDict.TryGetValue(typeof(T), out var existingCallback))
        {
            var newCallback = (Action<T>)existingCallback - callback;
            if (newCallback == null)
                _eventDict.Remove(typeof(T));
            else
                _eventDict[typeof(T)] = newCallback;
        }
    }

    /// <summary>
    /// 触发事件
    /// </summary>
    public static void Trigger<T>(T eventData) where T : class
    {
        if (_eventDict.TryGetValue(typeof(T), out var callback))
        {
            ((Action<T>)callback)?.Invoke(eventData);
        }
    }
}


