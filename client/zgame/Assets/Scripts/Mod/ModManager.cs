using System;
using System.Collections.Generic;

namespace ZGame.Mod
{
    /// <summary>
    /// Mod管理器单例，用于管理所有Mod实例
    /// </summary>
    public class ModManager : Singleton<ModManager>
    {
        /// <summary>
        /// 存储所有已注册的Mod实例
        /// </summary>
        private Dictionary<Type, ModBase> _mods = new Dictionary<Type, ModBase>();

        /// <summary>
        /// 注册一个Mod实例
        /// </summary>
        /// <typeparam name="T">Mod类型</typeparam>
        /// <param name="mod">Mod实例</param>
        public void RegisterMod<T>(T mod) where T : ModBase
        {
            Type type = typeof(T);
            if (!_mods.ContainsKey(type))
            {
                _mods[type] = mod;
                Console.WriteLine($"Mod {type.Name} registered successfully");
            }
            else
            {
                Console.WriteLine($"Mod {type.Name} is already registered");
            }
        }

        /// <summary>
        /// 获取指定类型的Mod实例
        /// </summary>
        /// <typeparam name="T">Mod类型</typeparam>
        /// <returns>Mod实例</returns>
        public T GetMod<T>() where T : ModBase
        {
            Type type = typeof(T);
            if (_mods.ContainsKey(type))
            {
                return (T)_mods[type];
            }
            
            Console.WriteLine($"Mod {type.Name} not found");
            return null;
        }

        /// <summary>
        /// 检查是否存在指定类型的Mod实例
        /// </summary>
        /// <typeparam name="T">Mod类型</typeparam>
        /// <returns>是否存在</returns>
        public bool HasMod<T>() where T : ModBase
        {
            Type type = typeof(T);
            return _mods.ContainsKey(type);
        }

        /// <summary>
        /// 注销指定类型的Mod实例
        /// </summary>
        /// <typeparam name="T">Mod类型</typeparam>
        public void UnregisterMod<T>() where T : ModBase
        {
            Type type = typeof(T);
            if (_mods.ContainsKey(type))
            {
                // 调用Mod的销毁方法
                _mods[type].OnDestroyMod();
                _mods.Remove(type);
                Console.WriteLine($"Mod {type.Name} unregistered successfully");
            }
            else
            {
                Console.WriteLine($"Mod {type.Name} not found for unregistering");
            }
        }

        /// <summary>
        /// 启用所有已注册的Mod
        /// </summary>
        public void EnableAllMods()
        {
            foreach (var mod in _mods.Values)
            {
                if (!mod.IsEnabled)
                {
                    mod.Enable();
                }
            }
            Console.WriteLine("All mods enabled");
        }

        /// <summary>
        /// 禁用所有已注册的Mod
        /// </summary>
        public void DisableAllMods()
        {
            foreach (var mod in _mods.Values)
            {
                if (mod.IsEnabled)
                {
                    mod.Disable();
                }
            }
            Console.WriteLine("All mods disabled");
        }

        /// <summary>
        /// 更新所有已注册的Mod
        /// </summary>
        public void UpdateAllMods()
        {
            foreach (var mod in _mods.Values)
            {
                if (mod.IsEnabled)
                {
                    mod.OnUpdate();
                }
            }
        }

        /// <summary>
        /// 销毁管理器并清理所有Mod
        /// </summary>
        public void Destroy()
        {
            foreach (var mod in _mods.Values)
            {
                mod.OnDestroyMod();
            }
            _mods.Clear();
            Console.WriteLine("ModManager destroyed");
        }
    }
}