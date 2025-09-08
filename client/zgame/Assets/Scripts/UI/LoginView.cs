using UnityEngine;
using ZGame.Mod;

public class LoginView
{
    private const string CanvasName = "Login";
    private const string CanvasPath = "Login";

    // 构造函数，初始化登录管理器
    public LoginView()
    {
        // 获取UIManager单例
        UIManager uiManager = UIManager.Instance;

        // 注册Canvas
        uiManager.RegisterCanvas(CanvasPath, true);

        // 给登录按钮绑定事件
        uiManager.AddButtonClickListener(CanvasName, "Button", OnLoginButtonClicked);
        uiManager.SetInputText(CanvasName, "Username", "admin");
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

        // 监听登录返回的事件

        ModManager.Instance.GetMod<ModLogin>().Login(username, password);
    }

}