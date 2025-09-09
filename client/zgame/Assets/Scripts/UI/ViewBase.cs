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
    public class ViewBase
    {
        protected string canvasName;
        protected string canvasPath;

        // 构造函数，初始化ViewBase
        public ViewBase(string canvasPath, string canvasName = null)
        {
            this.canvasPath = canvasPath;
            if (string.IsNullOrEmpty(canvasName))
            {
                canvasName = GetType().Name;
                // 去掉后缀"View"
                if (canvasName.EndsWith("View"))
                {
                    canvasName = canvasName[..^4];
                }
            }
            this.canvasName = canvasName;
        }

        // 获取指定路径的UI组件
        protected T GetUIComponent<T>(string path) where T : Component
        {
            return UIManager.Instance.GetUIComponent<T>(canvasName, path);
        }

        // 获取指定路径的按钮组件
        protected Button GetButton(string path)
        {
            return UIManager.Instance.GetButton(canvasName, path);
        }

        // 给指定路径的按钮绑定点击事件
        protected void AddButtonClickListener(string path, Action action)
        {
            UIManager.Instance.AddButtonClickListener(canvasName, path, action);
        }

        // 获取指定路径的输入框组件
        protected TMP_InputField GetInputField(string path)
        {
            return UIManager.Instance.GetInputField(canvasName, path);
        }

        // 获取指定路径输入框的文本内容
        protected string GetInputText(string path)
        {
            return UIManager.Instance.GetInputText(canvasName, path);
        }

        // 设置指定路径输入框的文本内容
        protected void SetInputText(string path, string text)
        {
            UIManager.Instance.SetInputText(canvasName, path, text);
        }

        // 获取指定路径的TextMeshPro文本组件
        protected TMP_Text GetTMPText(string path)
        {
            return UIManager.Instance.GetTMPText(canvasName, path);
        }

        // 获取指定路径TextMeshPro文本的内容
        protected string GetText(string path)
        {
            return UIManager.Instance.GetText(canvasName, path);
        }

        // 设置指定路径TextMeshPro文本的内容
        protected void SetText(string path, string text)
        {
            UIManager.Instance.SetText(canvasName, path, text);
        }

        // 显示Canvas
        protected void ShowCanvas()
        {
            UIManager.Instance.ShowCanvas(canvasName);
        }

        // 隐藏Canvas
        protected void HideCanvas()
        {
            UIManager.Instance.HideCanvas(canvasName);
        }

        // 注册Canvas
        protected void RegisterCanvas(bool setAsActive = false)
        {
            UIManager.Instance.RegisterCanvas(canvasName, setAsActive);
        }
    }
}