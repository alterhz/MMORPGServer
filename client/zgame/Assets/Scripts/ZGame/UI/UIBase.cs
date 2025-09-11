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
    public abstract class UIBase
    {
        public string CanvasName { get; private set; }
        public string CanvasPath { get; private set; }

        public Canvas Canvas { get; set; }

        // 构造函数，初始化ViewBase
        public UIBase(string canvasPath, string canvasName = null)
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

        // 获取指定路径的UI组件
        protected T GetUIComponent<T>(string path) where T : Component
        {
            Transform target = Canvas.transform.Find(path);
            if (target != null)
            {
                if (target.TryGetComponent<T>(out var component))
                {
                    return component;
                }
                else
                {
                    Debug.LogError($"在路径 {path} 未找到{typeof(T).Name}组件.target={target}");
                }
            }
            else
            {
                Debug.LogError($"在Canvas {CanvasName} 下未找到路径为 {path} 的UI元素");
            }
            return null;
        }

        // 获取指定路径的按钮组件
        protected Button GetButton(string path)
        {
            return GetUIComponent<Button>(path);
        }

        // 给指定路径的按钮绑定点击事件
        protected void AddButtonClickListener(string path, Action action)
        {
            Button button = GetButton(path);
            if (button != null)
            {
                button.onClick.RemoveAllListeners();
                button.onClick.AddListener(() => action());
            }
        }

        // 获取指定路径的输入框组件
        protected TMP_InputField GetInputField(string path)
        {
            return GetUIComponent<TMP_InputField>(path);
        }

        // 获取指定路径输入框的文本内容
        protected string GetInputText(string path)
        {
            return UIManager.Instance.GetInputText(CanvasName, path);
        }

        // 设置指定路径输入框的文本内容
        protected void SetInputText(string path, string text)
        {
            TMP_InputField inputField = GetInputField(path);
            if (inputField != null)
            {
                inputField.text = text;
            }
        }

        // 获取指定路径的TextMeshPro文本组件
        protected TMP_Text GetTMPText(string path)
        {
            return GetUIComponent<TMP_Text>(path);
        }

        // 获取指定路径TextMeshPro文本的内容
        protected string GetText(string path)
        {
            TMP_Text tmpText = GetTMPText(path);
            if (tmpText != null)
            {
                return tmpText.text;
            }
            return string.Empty;
        }

        // 设置指定路径TextMeshPro文本的内容
        protected void SetText(string path, string text)
        {
            TMP_Text tmpText = GetTMPText(path);
            if (tmpText != null)
            {
                tmpText.text = text;
            }
        }

        // 设置指定路径按钮的文本内容
        protected void SetButtonText(string path, string text)
        {
            Button button = GetButton(path);
            if (button != null)
            {
                TMP_Text textComponent = button.GetComponentInChildren<TMP_Text>();
                if (textComponent != null)
                {
                    textComponent.text = text;
                }
                else
                {
                    Debug.LogError($"在按钮 {path} 下未找到TextMeshPro文本组件");
                }
            }
        }

        
        protected void ShowComponent(string path)
        {
            Transform target = Canvas.transform.Find(path);
            if (target != null)
            {
                target.gameObject.SetActive(true);
            }
            else
            {
                Debug.LogError($"在Canvas {CanvasName} 下未找到路径为 {path} 的UI元素");
            }
        }

        protected void HideComponent(string path)
        {
            Transform target = Canvas.transform.Find(path);
            if (target != null)
            {
                target.gameObject.SetActive(false);
            }
            else
            {
                Debug.LogError($"在Canvas {CanvasName} 下未找到路径为 {path} 的UI元素");
            }
        }

        protected void ToggleComponent(string path)
        {
            Transform target = Canvas.transform.Find(path);
            if (target != null)
            {
                target.gameObject.SetActive(!target.gameObject.activeSelf);
            }
            else
            {
                Debug.LogError($"在Canvas {CanvasName} 下未找到路径为 {path} 的UI元素");
            }
        }

    }
}