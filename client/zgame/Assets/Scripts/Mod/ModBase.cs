using System;
using System.Collections.Generic;
using System.Reflection;
using ZGame;

namespace ZGame
{
    /// <summary>
    /// 所有Mod类的基类，提供Mod的基本生命周期和通用功能
    /// </summary>
    public abstract class ModBase
    {
        /// <summary>
        /// 存储当前Mod中通过ProtoListener特性注册的方法信息
        /// </summary>
        private readonly Dictionary<Type, MethodInfo> registeredHandlers = new();

        /// <summary>
        /// Mod的唯一标识符
        /// </summary>
        public string ModId { get; private set; }

        /// <summary>
        /// Mod是否已启用
        /// </summary>
        public bool IsEnabled { get; private set; }

        /// <summary>
        /// Mod是否已初始化
        /// </summary>
        public abstract void Initialize();

        /// <summary>
        /// 启用Mod
        /// </summary>
        public virtual void Enable()
        {
            IsEnabled = true;
            RegisterProtoListeners();
            OnEnable();
        }

        /// <summary>
        /// 禁用Mod
        /// </summary>
        public virtual void Disable()
        {
            UnregisterProtoListeners();
            IsEnabled = false;
            OnDisable();
        }

        /// <summary>
        /// Mod更新方法
        /// </summary>
        public virtual void OnUpdate()
        {
            // 子类可以重写此方法以实现特定逻辑
        }

        /// <summary>
        /// Mod销毁时调用的方法
        /// </summary>
        public virtual void OnDestroyMod()
        {
            if (IsEnabled)
            {
                Disable();
            }
            OnDestroy();
        }

        /// <summary>
        /// 当Mod被启用时调用
        /// </summary>
        protected abstract void OnEnable();

        /// <summary>
        /// 当Mod被禁用时调用
        /// </summary>
        protected abstract void OnDisable();

        /// <summary>
        /// 当Mod被销毁时调用
        /// </summary>
        protected virtual void OnDestroy()
        {
            // 子类可以重写此方法以实现特定逻辑
        }

        public T GetMod<T>() where T : ModBase
        {
            return ModManager.Instance.GetMod<T>();
        }

        /// <summary>
        /// 扫描当前类中带有ProtoListener特性的方法并自动注册
        /// </summary>
        public void ScanProtoListeners()
        {
            Type type = this.GetType();
            MethodInfo[] methods = type.GetMethods(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);

            foreach (MethodInfo method in methods)
            {
                ProtoListener protoListenerAttr = method.GetCustomAttribute<ProtoListener>();
                if (protoListenerAttr != null)
                {
                    ParameterInfo[] parameters = method.GetParameters();
                    if (parameters.Length == 1)
                    {
                        Type paramType = parameters[0].ParameterType;
                        // paramType包含Proto注解
                        if (!paramType.IsDefined(typeof(Proto), false))
                        {
                            throw new InvalidOperationException($"方法 {method.Name} 的参数类型 {paramType.Name} 未标记为 Proto");
                        }

                        // 重复注册
                        if (registeredHandlers.ContainsKey(paramType))
                        {
                            throw new InvalidOperationException($"协议类型 {paramType.Name} 已经被注册，不能重复注册");
                        }
                        
                        // 注册处理器
                        registeredHandlers[paramType] = method;
                        LogUtils.Log($"自动注册协议监听器: {type.Name}.{method.Name}({paramType.Name})");
                    }
                    else
                    {
                        // 抛出异常
                        throw new InvalidOperationException($"方法 {method.Name} 必须有且仅有一个参数才能使用 ProtoListener 特性");
                    }
                }
            }
        }

        /// <summary>
        /// 扫描当前类中带有ProtoListener特性的方法并自动注册
        /// </summary>
        private void RegisterProtoListeners()
        {
            foreach (var kvp in registeredHandlers)
            {
                ClientManager.Instance.RegisterProto(kvp.Key, this, kvp.Value);
            }
        }

        /// <summary>
        /// 注销所有通过ProtoListener特性注册的协议处理器
        /// </summary>
        private void UnregisterProtoListeners()
        {
            foreach (var kvp in registeredHandlers)
            {
                ClientManager.Instance.UnregisterProto(kvp.Key, this, kvp.Value);
            }
        }
    }
}