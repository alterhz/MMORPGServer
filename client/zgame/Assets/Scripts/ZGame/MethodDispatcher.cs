using System;
using System.Collections.Generic;
using System.Reflection;
using Unity.VisualScripting;

namespace ZGame
{

    /// <summary>
    /// 事件分发管理器基类，用于管理基于字符串键和Action<T>值的事件系统
    /// 支持继承，支持同一事件注册多个Action，避免重复注册
    /// </summary>
    public class MethodDispatcher
    {
        /// <summary>
        /// 存储事件处理器的字典 <事件名称, <对象，方法列表>>
        /// </summary>
        private readonly Dictionary<string, Dictionary<object, List<MethodInfo>>> _EventMethodDic = new();


        public void RegisterMethod(string eventType, object obj, MethodInfo methodInfo)
        {
            if (!_EventMethodDic.ContainsKey(eventType))
            {
                _EventMethodDic[eventType] = new Dictionary<object, List<MethodInfo>>();
            }

            // 检查是否已存在相同的处理器，避免重复注册
            if (!_EventMethodDic[eventType].ContainsKey(obj))
            {
                _EventMethodDic[eventType][obj] = new List<MethodInfo>();
            }

            if (!_EventMethodDic[eventType][obj].Contains(methodInfo))
            {
                _EventMethodDic[eventType][obj].Add(methodInfo);
            }
        }

        public void UnregisterMethod(string eventType, object obj, MethodInfo methodInfo)
        {
            if (_EventMethodDic.ContainsKey(eventType))
            {
                if (_EventMethodDic[eventType].ContainsKey(obj))
                {
                    _EventMethodDic[eventType][obj].Remove(methodInfo);
                    if (_EventMethodDic[eventType][obj].Count == 0)
                    {
                        _EventMethodDic[eventType].Remove(obj);
                    }
                }

                if (_EventMethodDic[eventType].Count == 0)
                {
                    _EventMethodDic.Remove(eventType);
                }
            }
        }

        /// <summary>
        /// 触发事件
        /// </summary>
        /// <param name="eventType">事件类型标识符</param>
        /// <param name="param">事件数据</param>
        public void InvokeMethod(string eventType, object param)
        {
            if (_EventMethodDic.ContainsKey(eventType))
            {
                // 便利_EventMethodDic[eventType]
                foreach (var objMethodsPair in _EventMethodDic[eventType])
                {
                    var obj = objMethodsPair.Key;
                    var methods = objMethodsPair.Value;

                    foreach (var method in methods)
                    {
                        try
                        {
                            method.Invoke(obj, new object[] { param });
                        }
                        catch (Exception ex)
                        {
                            LogUtils.LogError($"事件 {eventType} 处理器 {method.Name} 执行失败: {ex.Message}");
                        }
                    }
                }
            }
        }

        /// <summary>
        /// 清除指定事件类型的所有处理器
        /// </summary>
        /// <param name="eventType">事件类型标识符</param>
        public void ClearEvent(string eventType)
        {
            if (_EventMethodDic.ContainsKey(eventType))
            {
                _EventMethodDic.Remove(eventType);
            }
        }

        /// <summary>
        /// 清除所有事件处理器
        /// </summary>
        public void ClearAllEvents()
        {
            _EventMethodDic.Clear();
        }
    }
}