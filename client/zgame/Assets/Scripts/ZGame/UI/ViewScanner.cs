using System;
using System.Collections.Generic;
using System.Reflection;
using UnityEngine;

namespace ZGame
{
    /// <summary>
    /// View扫描器，用于自动扫描和注册所有ViewBase的子类
    /// </summary>
    public class ViewScanner
    {
        /// <summary>
        /// 存储所有扫描到的View类型
        /// </summary>
        private static readonly List<Type> ViewTypes = new();

        /// <summary>
        /// 扫描所有View中包含EventListener特性的函数
        /// </summary>
        private static readonly Dictionary<Type, Dictionary<string, MethodInfo>> EventListenerMethods = new();

        /// <summary>
        /// 扫描程序集中所有ViewBase的子类并注册到UIManager
        /// </summary>
        public static void ScanAndRegisterViews()
        {
            // 获取当前程序集
            Assembly assembly = Assembly.GetExecutingAssembly();

            // 获取所有类型
            Type[] types = assembly.GetTypes();

            ViewTypes.Clear();

            // 遍历所有类型，查找ViewBase的子类
            foreach (Type type in types)
            {
                // 检查是否为ViewBase的非抽象子类
                if (type.IsSubclassOf(typeof(ViewBase)) && !type.IsAbstract)
                {
                    ViewTypes.Add(type);
                }
            }

            // 注册所有找到的View类型
            foreach (Type viewType in ViewTypes)
            {
                RegisterView(viewType);
                ScanAndRegisterEventListeners(viewType);
            }
        }

        /// <summary>
        /// 注册指定类型的View
        /// </summary>
        /// <param name="viewType">要注册的View类型</param>
        private static void RegisterView(Type viewType)
        {
            try
            {
                // 创建View实例
                ViewBase viewInstance = (ViewBase)Activator.CreateInstance(viewType);

                // 注册到UIManager
                UIManager.Instance.RegisterCanvas(viewInstance);
            }
            catch (Exception ex)
            {
                Debug.LogError($"Failed to register view {viewType.Name}: {ex.Message}");
            }
        }

        /// <summary>
        /// 扫描并注册指定View类型中带有EventListener特性的所有方法
        /// </summary>
        /// <param name="viewType">要扫描的View类型</param>
        private static void ScanAndRegisterEventListeners(Type viewType)
        {
            // 事件key=第一个参数类型Name，value=MethodInfo
            Dictionary<string, MethodInfo> methods = new();
            MethodInfo[] methodInfos = viewType.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.DeclaredOnly);

            foreach (var method in methodInfos)
            {
                // 假设有一个EventListenerAttribute用于标记事件监听方法
                if (Attribute.IsDefined(method, typeof(EventListener)))
                {
                    ParameterInfo[] parameters = method.GetParameters();
                    if (parameters.Length == 1)
                    {
                        string eventKey = parameters[0].ParameterType.Name;
                        if (!methods.ContainsKey(eventKey))
                        {
                            methods[eventKey] = method;
                            Debug.Log($"Registered event listener: {viewType.Name}.{method.Name} for event {eventKey}");
                        }
                        else
                        {
                            throw new Exception($"Duplicate event listener for event {eventKey}");
                        }
                    }
                    else
                    {
                        throw new Exception($"Event listener method {method} must have exactly one parameter");
                    }
                }
            }

            if (methods.Count > 0)
            {
                EventListenerMethods[viewType] = methods;
            }
        }
        
        /// <summary>
        /// 获取指定View类型中带有EventListener特性的所有方法
        /// </summary>
        /// <param name="viewType">类型</param>
        /// <returns></returns>
        public static Dictionary<string, MethodInfo> GetEventListenerMethods(Type viewType)
        {
            if (EventListenerMethods.TryGetValue(viewType, out var methods))
            {
                return methods;
            }
            return new Dictionary<string, MethodInfo>();
        }
        
    }
}