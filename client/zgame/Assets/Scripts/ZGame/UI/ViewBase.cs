using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

namespace ZGame
{
    /// <summary>
    /// UI视图基类，提供常用的UI操作方法
    /// </summary>
    public abstract class ViewBase
    {
        public string CanvasName { get; private set; }
        public string CanvasPath { get; private set; }

        public Canvas Canvas { get; set; }

        // 构造函数，初始化ViewBase
        public ViewBase(string canvasPath, string canvasName = null)
        {
            this.CanvasPath = canvasPath;
            if (string.IsNullOrEmpty(canvasName))
            {
                canvasName = GetType().Name;
                // 去掉后缀"View"
                if (canvasName.EndsWith("View"))
                {
                    canvasName = canvasName[..^4];
                }
            }
            this.CanvasName = canvasName;
        }

        /// <summary>
        /// 初始化
        /// </summary>
        public abstract void OnInitialize();

        public abstract void OnShow();
        public abstract void OnHide();

        // 获取指定路径的UI组件
        protected T GetUIComponent<T>(string path) where T : Component
        {
            return UIManager.Instance.GetUIComponent<T>(CanvasName, path);
        }

        // 获取指定路径的按钮组件
        protected Button GetButton(string path)
        {
            return UIManager.Instance.GetButton(CanvasName, path);
        }

        // 给指定路径的按钮绑定点击事件
        protected void AddButtonClickListener(string path, Action action)
        {
            UIManager.Instance.AddButtonClickListener(CanvasName, path, action);
        }

        // 获取指定路径的输入框组件
        protected TMP_InputField GetInputField(string path)
        {
            return UIManager.Instance.GetInputField(CanvasName, path);
        }

        // 获取指定路径输入框的文本内容
        protected string GetInputText(string path)
        {
            return UIManager.Instance.GetInputText(CanvasName, path);
        }

        // 设置指定路径输入框的文本内容
        protected void SetInputText(string path, string text)
        {
            UIManager.Instance.SetInputText(CanvasName, path, text);
        }

        // 获取指定路径的TextMeshPro文本组件
        protected TMP_Text GetTMPText(string path)
        {
            return UIManager.Instance.GetTMPText(CanvasName, path);
        }

        // 获取指定路径TextMeshPro文本的内容
        protected string GetText(string path)
        {
            return UIManager.Instance.GetText(CanvasName, path);
        }

        // 设置指定路径TextMeshPro文本的内容
        protected void SetText(string path, string text)
        {
            UIManager.Instance.SetText(CanvasName, path, text);
        }

        // 设置指定路径按钮的文本内容
        protected void SetButtonText(string path, string text)
        {
            UIManager.Instance.SetButtonText(CanvasName, path, text);
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

        protected void ShowComponent(string path)
        {
            UIManager.Instance.SetComponentActive(CanvasName, path, true);
        }

        protected void HideComponent(string path)
        {
            UIManager.Instance.SetComponentActive(CanvasName, path, false);
        }

        protected void ToggleComponent(string path)
        {
            UIManager.Instance.ToggleComponentActive(CanvasName, path);
        }

        // 注册Canvas
        protected void RegisterCanvas(bool setAsActive = false)
        {
            UIManager.Instance.RegisterCanvas(this, setAsActive);
        }

        public T GetMod<T>() where T : ModBase
        {
            return ModManager.Instance.GetMod<T>();
        }
    }
}