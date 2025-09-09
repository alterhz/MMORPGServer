using System;
using System.Collections.Generic;
using System.Reflection;

namespace ZGame
{
    /// <summary>
    /// Mod扫描器，用于自动扫描和注册所有ModBase的子类
    /// </summary>
    public class ModScanner
    {
        /// <summary>
        /// 扫描程序集中所有ModBase的子类并注册到ModManager
        /// </summary>
        public static void ScanAndRegisterMods()
        {
            // 获取当前程序集
            Assembly assembly = Assembly.GetExecutingAssembly();
            
            // 获取所有类型
            Type[] types = assembly.GetTypes();
            
            // 存储所有ModBase的子类类型
            List<Type> modTypes = new List<Type>();
            
            // 遍历所有类型，查找ModBase的子类
            foreach (Type type in types)
            {
                // 检查是否为ModBase的非抽象子类
                if (type.IsSubclassOf(typeof(ModBase)) && !type.IsAbstract)
                {
                    modTypes.Add(type);
                }
            }
            
            // 注册所有找到的Mod类型
            foreach (Type modType in modTypes)
            {
                RegisterMod(modType);
            }
        }
        
        /// <summary>
        /// 注册指定类型的Mod
        /// </summary>
        /// <param name="modType">要注册的Mod类型</param>
        private static void RegisterMod(Type modType)
        {
            try
            {
                // 创建Mod实例
                ModBase modInstance = (ModBase)Activator.CreateInstance(modType);
                
                // 通过反射调用ModManager的RegisterMod方法
                ModManager.Instance.RegisterMod(modInstance);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to register mod {modType.Name}: {ex.Message}");
            }
        }
    }
}