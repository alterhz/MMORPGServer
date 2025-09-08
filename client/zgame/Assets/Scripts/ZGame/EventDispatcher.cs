using System;
using System.Collections.Generic;

/// <summary>
/// 事件分发管理器基类，用于管理基于字符串键和Action<T>值的事件系统
/// 支持继承，支持同一事件注册多个Action，避免重复注册
/// </summary>
public class EventDispatcher<T> where T : class
{
    /// <summary>
    /// 存储事件处理器的字典，键为事件名，值为事件处理委托列表
    /// </summary>
    private readonly Dictionary<string, List<Action<T>>> _eventHandlers = new();

    /// <summary>
    /// 注册事件处理器
    /// </summary>
    /// <param name="eventType">事件类型标识符</param>
    /// <param name="handler">事件处理器</param>
    public void RegisterEvent(string eventType, Action<T> handler)
    {
        if (!_eventHandlers.ContainsKey(eventType))
        {
            _eventHandlers[eventType] = new List<Action<T>>();
        }

        // 检查是否已存在相同的处理器，避免重复注册
        if (!_eventHandlers[eventType].Contains(handler))
        {
            _eventHandlers[eventType].Add(handler);
        }
    }

    /// <summary>
    /// 注销事件处理器
    /// </summary>
    /// <param name="eventType">事件类型标识符</param>
    /// <param name="handler">要注销的事件处理器</param>
    public void UnregisterEvent(string eventType, Action<T> handler)
    {
        if (_eventHandlers.ContainsKey(eventType))
        {
            _eventHandlers[eventType].Remove(handler);

            // 如果该事件类型没有处理器了，则移除该事件类型
            if (_eventHandlers[eventType].Count == 0)
            {
                _eventHandlers.Remove(eventType);
            }
        }
    }

    /// <summary>
    /// 触发事件
    /// </summary>
    /// <param name="eventType">事件类型标识符</param>
    /// <param name="data">事件数据</param>
    public void DispatchEvent(string eventType, T data)
    {
        if (_eventHandlers.ContainsKey(eventType))
        {
            // 创建一个副本以防止在遍历过程中修改集合
            var handlers = new List<Action<T>>(_eventHandlers[eventType]);

            foreach (var handler in handlers)
            {
                handler(data);
            }
        }
    }

    /// <summary>
    /// 清除指定事件类型的所有处理器
    /// </summary>
    /// <param name="eventType">事件类型标识符</param>
    public void ClearEvent(string eventType)
    {
        if (_eventHandlers.ContainsKey(eventType))
        {
            _eventHandlers.Remove(eventType);
        }
    }

    /// <summary>
    /// 清除所有事件处理器
    /// </summary>
    public void ClearAllEvents()
    {
        _eventHandlers.Clear();
    }
}