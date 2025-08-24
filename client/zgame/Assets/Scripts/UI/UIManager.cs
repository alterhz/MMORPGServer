using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class UIManager : MonoBehaviour
{
    // 单例实例
    private static UIManager _instance;
    public static UIManager Instance
    {
        get
        {
            if (_instance == null)
            {
                _instance = FindObjectOfType<UIManager>();
                if (_instance == null)
                {
                    GameObject go = new GameObject("UIManager");
                    _instance = go.AddComponent<UIManager>();
                }
            }
            return _instance;
        }
    }

    // 存储所有Canvas的字典
    private Dictionary<string, Canvas> canvasDictionary = new Dictionary<string, Canvas>();
    
    // 当前活动的Canvas名称
    private string currentActiveCanvas = "";

    // Canvas历史堆栈，用于返回功能
    private Stack<string> canvasHistory = new Stack<string>();

    // UI根节点
    private Transform uiRoot;

    void Awake()
    {
        if (_instance != null && _instance != this)
        {
            Destroy(gameObject);
            return;
        }
        
        _instance = this;
        DontDestroyOnLoad(gameObject);
        
        // 初始化UI根节点
        InitializeUIRoot();
    }

    // 初始化UI根节点
    private void InitializeUIRoot()
    {
        GameObject uiRootGO = GameObject.Find("UI");
        if (uiRootGO == null)
        {
            Debug.LogError("UI根节点未找到！请确保有一个名为'UI'的GameObject");
            return;
        }
        uiRoot = uiRootGO.transform;
    }

    // 通过路径获取Canvas
    public Canvas GetCanvas(string canvasPath)
    {
        if (uiRoot == null)
        {
            Debug.LogError("UI根节点未初始化");
            return null;
        }

        // 查找Canvas路径
        Transform canvasTransform = uiRoot.Find(canvasPath);
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
    public void RegisterCanvas(string canvasPath, bool setAsActive = false)
    {
        Canvas canvas = GetCanvas(canvasPath);
        if (canvas == null) return;
        
        string canvasName = canvas.gameObject.name;
        if (!canvasDictionary.ContainsKey(canvasName))
        {
            canvasDictionary.Add(canvasName, canvas);
            
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
        if (canvasDictionary.TryGetValue(canvasName, out Canvas canvas))
        {
            // 隐藏当前活动的Canvas
            if (!string.IsNullOrEmpty(currentActiveCanvas))
            {
                HideCanvas(currentActiveCanvas);
            }
            
            // 将当前Canvas加入历史
            if (!string.IsNullOrEmpty(currentActiveCanvas))
            {
                canvasHistory.Push(currentActiveCanvas);
            }
            
            // 显示新Canvas
            canvas.gameObject.SetActive(true);
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
        if (canvasDictionary.TryGetValue(canvasName, out Canvas canvas))
        {
            canvas.gameObject.SetActive(false);
            
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
        if (canvasDictionary.TryGetValue(canvasName, out Canvas canvas))
        {
            canvas.gameObject.SetActive(!canvas.gameObject.activeSelf);
        }
        else
        {
            Debug.LogWarning($"未找到名为 {canvasName} 的Canvas");
        }
    }

    // 返回到上一个Canvas
    public void GoBack()
    {
        if (canvasHistory.Count > 0)
        {
            string previousCanvas = canvasHistory.Pop();
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
        if (canvasDictionary.TryGetValue(canvasName, out Canvas canvas))
        {
            Transform target = canvas.transform.Find(path);
            if (target != null)
            {
                T component = target.GetComponent<T>();
                if (component != null)
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
}