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
        /// 扫描程序集中所有ViewBase的子类并注册到UIManager
        /// </summary>
        public static void ScanAndRegisterViews()
        {
            // 获取当前程序集
            Assembly assembly = Assembly.GetExecutingAssembly();
            
            // 获取所有类型
            Type[] types = assembly.GetTypes();
            
            // 存储所有ViewBase的子类类型
            List<Type> viewTypes = new List<Type>();
            
            // 遍历所有类型，查找ViewBase的子类
            foreach (Type type in types)
            {
                // 检查是否为ViewBase的非抽象子类
                if (type.IsSubclassOf(typeof(ViewBase)) && !type.IsAbstract)
                {
                    viewTypes.Add(type);
                }
            }
            
            // 注册所有找到的View类型
            foreach (Type viewType in viewTypes)
            {
                RegisterView(viewType);
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
    }
}