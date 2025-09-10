using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

namespace ZGame
{
    /// <summary>
    /// UI管理器单例，负责管理所有Canvas和UI组件的显示与交互
    /// </summary>
    public class UIManager : Singleton<UIManager>
    {

        // 存储所有Canvas的字典
        private readonly Dictionary<string, ViewBase> _CanvasDictionary = new();

        // 当前活动的Canvas名称
        private string currentActiveCanvas = "";

        // Canvas历史堆栈，用于返回功能
        private readonly Stack<string> _CanvasHistory = new();

        // UI根节点
        private Transform _UiRoot;


        // 初始化UI根节点
        public void InitializeUIRoot()
        {
            GameObject uiRootGO = GameObject.Find("UI");
            if (uiRootGO == null)
            {
                Debug.LogError("UI根节点未找到！请确保有一个名为'UI'的GameObject");
                return;
            }
            _UiRoot = uiRootGO.transform;
        }

        // 通过路径获取Canvas
        public Canvas GetCanvas(string canvasPath)
        {
            if (_UiRoot == null)
            {
                Debug.LogError("UI根节点未初始化");
                return null;
            }

            // 查找Canvas路径
            Transform canvasTransform = _UiRoot.Find(canvasPath);
            if (canvasTransform == null)
            {
                Debug.LogWarning($"未找到路径为 {canvasPath} 的Canvas");
                return null;
            }

            Canvas canvas = canvasTransform.GetComponent<Canvas>();
            if (canvas == null)
            {
                Debug.LogWarning($"路径为 {canvasPath} 的对象没有Canvas组件");
                return null;
            }

            return canvas;
        }

        // 注册Canvas到管理器中
        public void RegisterCanvas(ViewBase viewBase, bool setAsActive = false)
        {
            Canvas canvas = GetCanvas(viewBase.CanvasPath);
            if (canvas == null) return;

            string canvasName = canvas.gameObject.name;
            if (!_CanvasDictionary.ContainsKey(canvasName))
            {
                viewBase.Canvas = canvas;
                _CanvasDictionary.Add(canvasName, viewBase);
                viewBase.OnInitialize();

                // 默认隐藏Canvas，除非明确设置为活动
                if (!setAsActive)
                {
                    canvas.gameObject.SetActive(false);
                }
                else
                {
                    // 隐藏当前活动的Canvas
                    if (!string.IsNullOrEmpty(currentActiveCanvas))
                    {
                        HideCanvas(currentActiveCanvas);
                    }

                    // 显示新Canvas
                    canvas.gameObject.SetActive(true);
                    viewBase.OnShow();
                    currentActiveCanvas = canvasName;
                }
            }
            else
            {
                Debug.LogWarning($"已存在同名Canvas: {canvasName}");
            }
        }

        // 显示指定名称的Canvas
        public void ShowCanvas(string canvasName)
        {
            if (_CanvasDictionary.TryGetValue(canvasName, out ViewBase viewBase))
            {
                // 隐藏当前活动的Canvas
                if (!string.IsNullOrEmpty(currentActiveCanvas))
                {
                    HideCanvas(currentActiveCanvas);
                }

                // 将当前Canvas加入历史
                if (!string.IsNullOrEmpty(currentActiveCanvas))
                {
                    _CanvasHistory.Push(currentActiveCanvas);
                }

                // 显示新Canvas
                viewBase.OnShow();
                viewBase.Canvas.gameObject.SetActive(true);
                currentActiveCanvas = canvasName;
            }
            else
            {
                Debug.LogWarning($"未找到名为 {canvasName} 的Canvas");
            }
        }

        // 隐藏指定名称的Canvas
        public void HideCanvas(string canvasName)
        {
            if (_CanvasDictionary.TryGetValue(canvasName, out ViewBase viewBase))
            {
                viewBase.Canvas.gameObject.SetActive(false);
                viewBase.OnHide();

                // 如果隐藏的是当前活动Canvas，清空当前活动Canvas
                if (currentActiveCanvas == canvasName)
                {
                    currentActiveCanvas = "";
                }
            }
            else
            {
                Debug.LogWarning($"未找到名为 {canvasName} 极速Canvas");
            }
        }

        // 切换指定Canvas的显示状态
        public void ToggleCanvas(string canvasName)
        {
            if (_CanvasDictionary.TryGetValue(canvasName, out ViewBase viewBase))
            {
                if (viewBase.Canvas.gameObject.activeSelf)
                {
                    HideCanvas(canvasName);
                }
                else
                {
                    ShowCanvas(canvasName);
                }
                viewBase.Canvas.gameObject.SetActive(!viewBase.Canvas.gameObject.activeSelf);
            }
            else
            {
                Debug.LogWarning($"未找到名为 {canvasName} 的Canvas");
            }
        }

        // 返回到上一个Canvas
        public void GoBack()
        {
            if (_CanvasHistory.Count > 0)
            {
                string previousCanvas = _CanvasHistory.Pop();
                ShowCanvas(previousCanvas);
            }
            else
            {
                Debug.Log("没有更多的Canvas历史记录");
            }
        }

        // 获取指定路径的UI组件
        public T GetUIComponent<T>(string canvasName, string path) where T : Component
        {
            if (_CanvasDictionary.TryGetValue(canvasName, out ViewBase viewBase))
            {
                Transform target = viewBase.Canvas.transform.Find(path);
                if (target != null)
                {
                    if (target.TryGetComponent<T>(out var component))
                    {
                        return component;
                    }
                    else
                    {
                        Debug.LogWarning($"在路径 {path} 未找到{typeof(T).Name}组件.target={target}");
                    }
                }
                else
                {
                    Debug.LogWarning($"在Canvas {canvasName} 下未找到路径为 {path} 的UI元素");
                }
            }
            else
            {
                Debug.LogWarning($"未找到名为 {canvasName} 的Canvas");
            }
            return null;
        }

        // 获取指定路径的按钮组件
        public Button GetButton(string canvasName, string path)
        {
            return GetUIComponent<Button>(canvasName, path);
        }

        // 给指定路径的按钮绑定点击事件
        public void AddButtonClickListener(string canvasName, string path, Action action)
        {
            Button button = GetButton(canvasName, path);
            if (button != null)
            {
                button.onClick.RemoveAllListeners();
                button.onClick.AddListener(() => action());
            }
        }

        // 获取指定路径的输入框组件
        public TMP_InputField GetInputField(string canvasName, string path)
        {
            return GetUIComponent<TMP_InputField>(canvasName, path);
        }

        // 获取指定路径输入框的文本内容
        public string GetInputText(string canvasName, string path)
        {
            TMP_InputField inputField = GetInputField(canvasName, path);
            if (inputField != null)
            {
                return inputField.text;
            }
            return string.Empty;
        }

        // 设置指定路径输入框的文本内容
        public void SetInputText(string canvasName, string path, string text)
        {
            TMP_InputField inputField = GetInputField(canvasName, path);
            if (inputField != null)
            {
                inputField.text = text;
            }
        }

        // 获取指定路径的TextMeshPro文本组件
        public TMP_Text GetTMPText(string canvasName, string path)
        {
            return GetUIComponent<TMP_Text>(canvasName, path);
        }

        // 获取指定路径TextMeshPro文本的内容
        public string GetText(string canvasName, string path)
        {
            TMP_Text tmpText = GetTMPText(canvasName, path);
            if (tmpText != null)
            {
                return tmpText.text;
            }
            return string.Empty;
        }

        // 设置指定路径TextMeshPro文本的内容
        public void SetText(string canvasName, string path, string text)
        {
            TMP_Text tmpText = GetTMPText(canvasName, path);
            if (tmpText != null)
            {
                tmpText.text = text;
            }
        }

        // 设置指定路径按钮的文本内容
        public void SetButtonText(string canvasName, string path, string text)
        {
            Button button = GetButton(canvasName, path);
            if (button != null)
            {
                TMP_Text textComponent = button.GetComponentInChildren<TMP_Text>();
                if (textComponent != null)
                {
                    textComponent.text = text;
                }
                else
                {
                    Debug.LogWarning($"在按钮 {path} 下未找到TextMeshPro文本组件");
                }
            }
        }

        // 设置指定路径组件的激活状态
        public void SetComponentActive(string canvasName, string path, bool active)
        {
            if (_CanvasDictionary.TryGetValue(canvasName, out ViewBase viewBase))
            {
                Transform target = viewBase.Canvas.transform.Find(path);
                if (target != null)
                {
                    target.gameObject.SetActive(active);
                }
                else
                {
                    Debug.LogWarning($"在Canvas {canvasName} 下未找到路径为 {path} 的UI元素");
                }
            }
            else
            {
                Debug.LogWarning($"未找到名为 {canvasName} 的Canvas");
            }
        }

        // 切换指定路径组件的激活状态
        public void ToggleComponentActive(string canvasName, string path)
        {
            if (_CanvasDictionary.TryGetValue(canvasName, out ViewBase viewBase))
            {
                Transform target = viewBase.Canvas.transform.Find(path);
                if (target != null)
                {
                    target.gameObject.SetActive(!target.gameObject.activeSelf);
                }
                else
                {
                    Debug.LogWarning($"在Canvas {canvasName} 下未找到路径为 {path} 的UI元素");
                }
            }
            else
            {
                Debug.LogWarning($"未找到名为 {canvasName} 的Canvas");
            }
        }
    }
}