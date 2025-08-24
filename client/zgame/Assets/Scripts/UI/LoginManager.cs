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

        // 注册消息处理器（自动反序列化为对象）
        ClientManager.Instance.RegisterHandler<LoginResponse>(1002, (response) => {
            if (response.code == 0)
            {
                Debug.Log("登录成功，用户ID: " + response.data);

                // 切换到主页
                uiManager.ShowCanvas("Main");
            }
            else
            {
                Debug.LogWarning("登录失败: " + response.message);
                // Text显示提示信息
                uiManager.SetText("Login", "Message", response.message);
            }
        });

        // 连接服务器，并发送登录请求
        ClientManager.Instance.Connect();
        ClientManager.Instance.Login(username, password);
    }

}