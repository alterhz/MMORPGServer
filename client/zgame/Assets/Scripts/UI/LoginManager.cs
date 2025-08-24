using System;
using UnityEngine;

public class LoginManager
{
    private const string CanvasName = "Login";
    private const string CanvasPath = "Login";

    // 构造函数，初始化登录管理器
    public LoginManager()
    {
        // 获取UIManager单例
        UIManager uiManager = UIManager.Instance;

        // 注册Canvas
        uiManager.RegisterCanvas(CanvasPath, true);

        // 给登录按钮绑定事件
        uiManager.AddButtonClickListener(CanvasName, "Button", OnLoginButtonClicked);
        string username = uiManager.GetInputText(CanvasName, "Username");
        Debug.Log(username);
    }

    private void OnLoginButtonClicked()
    {
        // 获取UIManager单例
        UIManager uiManager = UIManager.Instance;
        
        // 获取用户名和密码
        string username = uiManager.GetInputText(CanvasName, "Username");
        string password = uiManager.GetInputText(CanvasName, "Password");

        // 执行登录逻辑
        Debug.Log($"用户名: {username}, 密码: {password}");

        // 用户名和密码都是admin，登录成功
        if (username == "admin" && password == "admin")
        {
            // 登录成功后切换到主界面
            uiManager.ShowCanvas("Main");
        }
        
    }
}