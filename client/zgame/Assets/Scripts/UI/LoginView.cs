using UnityEngine;
using ZGame;

public class LoginView : ViewBase
{
    private const string CanvasName = "Login";
    private const string CanvasPath = "Login";

    // 构造函数，初始化登录管理器
    public LoginView()
    {
        RegisterCanvas(true);

        AddButtonClickListener("Button", OnLoginButtonClicked);
        SetInputText("Username", "admin");
        string username = GetInputText("Username");
        LogUtils.Log("account:" + username);

    }

    private void OnLoginButtonClicked()
    {

        // 获取用户名和密码
        string username = GetInputText("Username");
        string password = GetInputText("Password");

        // 执行登录逻辑
        LogUtils.Log($"Attempting login with Username: {username}, Password: {password}");

        // 监听登录返回的事件
        EventBus.Subscribe<LoginResultEvent>(OnLoginResponse);

        ModManager.Instance.GetMod<ModLogin>().Login(username, password);
    }

    private void OnLoginResponse(LoginResultEvent eventData)
    {
        if (eventData.IsSuccess)
        {
            // 切换到主页
            UIManager.Instance.ShowCanvas("Main");
            HideCanvas();
        }
        else
        {
            SetText("Message", eventData.Message);
        }
    }

}