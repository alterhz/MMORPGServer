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
    public class EventDispatcher
    {
        /// <summary>
        /// 存储事件处理器的字典，键为事件名，值为事件处理委托列表
        /// </summary>
        private readonly Dictionary<string, List<MethodInfo>> _MethodDic = new();


        public void RegisterEvent(string eventType, MethodInfo handler)
        {
            if (!_MethodDic.ContainsKey(eventType))
            {
                _MethodDic[eventType] = new List<MethodInfo>();
            }

            // 检查是否已存在相同的处理器，避免重复注册
            if (!_MethodDic[eventType].Contains(handler))
            {
                _MethodDic[eventType].Add(handler);
            }
        }

        public void UnregisterEvent(string eventType, MethodInfo handler)
        {
            if (_MethodDic.ContainsKey(eventType))
            {
                _MethodDic[eventType].Remove(handler);

                // 如果该事件类型没有处理器了，则移除该事件类型
                if (_MethodDic[eventType].Count == 0)
                {
                    _MethodDic.Remove(eventType);
                }
            }
        }

        /// <summary>
        /// 触发事件
        /// </summary>
        /// <param name="eventType">事件类型标识符</param>
        /// <param name="data">事件数据</param>
        public void DispatchEvent<T>(string eventType, object obj, T data)
        {
            if (_MethodDic.ContainsKey(eventType))
            {
                // 创建一个副本以防止在遍历过程中修改集合
                var handlers = new List<MethodInfo>(_MethodDic[eventType]);

                foreach (var handler in handlers)
                {
                    handler.Invoke(obj, new object[] { data });
                }
            }
        }

        public void DispatchEvent2<T>(string eventType, Func<MethodInfo, object> func, T data)
        {
            if (_MethodDic.ContainsKey(eventType))
            {
                // 创建一个副本以防止在遍历过程中修改集合
                var methods = new List<MethodInfo>(_MethodDic[eventType]);

                foreach (var mothod in methods)
                {
                    mothod.Invoke(func(mothod), new object[] { data });
                }
            }
        }



        /// <summary>
        /// 清除指定事件类型的所有处理器
        /// </summary>
        /// <param name="eventType">事件类型标识符</param>
        public void ClearEvent(string eventType)
        {
            if (_MethodDic.ContainsKey(eventType))
            {
                _MethodDic.Remove(eventType);
            }
        }

        /// <summary>
        /// 清除所有事件处理器
        /// </summary>
        public void ClearAllEvents()
        {
            _MethodDic.Clear();
        }
    }
}