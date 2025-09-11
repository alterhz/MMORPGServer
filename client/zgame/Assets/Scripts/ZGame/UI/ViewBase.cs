using System;
using System.Reflection;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace ZGame
{
    /// <summary>
    /// View基类，继承自UIBase
    /// </summary>
    public abstract class ViewBase : UIBase
    {
        /// <summary>
        /// 存储当前Mod中通过ProtoListener特性注册的方法信息
        /// </summary>
        private readonly Dictionary<Type, MethodInfo> registeredHandlers = new();

        protected ViewBase(string canvasPath, string canvasName = null) : base(canvasPath, canvasName)
        {
        }

        protected abstract void OnShow();
        protected abstract void OnHide();

        public void Show()
        {
            Dictionary<string, MethodInfo> eventMethods = ViewScanner.GetEventListenerMethods(this.GetType());
            foreach (var methodPair in eventMethods)
            {
                EventManager.Instance.Register(methodPair.Key, this, methodPair.Value);
            }
            OnShow();
        }

        public void Hide()
        {
            OnHide();
            Dictionary<string, MethodInfo> eventMethods = ViewScanner.GetEventListenerMethods(this.GetType());
            foreach (var methodPair in eventMethods)
            {
                EventManager.Instance.Unregister(methodPair.Key, this, methodPair.Value);
            }
        }

// 注册Canvas
        protected void RegisterCanvas(bool setAsActive = false)
        {
            UIManager.Instance.RegisterCanvas(this, setAsActive);
        }

        protected void ShowCanvas(string canvasName)
        {
            UIManager.Instance.ShowCanvas(canvasName);
        }

        protected void HideCanvas(string canvasName)
        {
            UIManager.Instance.HideCanvas(canvasName);
        }

        // 显示Canvas
        protected void ShowCanvas()
        {
            UIManager.Instance.ShowCanvas(CanvasName);
        }

        // 隐藏Canvas
        protected void HideCanvas()
        {
            UIManager.Instance.HideCanvas(CanvasName);
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

    }
}