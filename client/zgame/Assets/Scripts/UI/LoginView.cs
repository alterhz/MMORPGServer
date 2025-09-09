using UnityEngine;
using ZGame;

public class LoginView : ViewBase
{
    // 构造函数，初始化登录管理器
    public LoginView() : base(ViewNames.LOGIN)
    {
    }

    public override void OnInitialize()
    {
        AddButtonClickListener("Button", OnLoginButtonClicked);
        SetInputText("Username", "admin");
        string username = GetInputText("Username");
        LogUtils.Log("account:" + username);
    }

    public override void OnShow()
    {
        LogUtils.Log("加载登录界面");
        // 监听登录返回的事件
        EventBus.Subscribe<LoginResultEvent>(OnLoginResponse);
    }

    public override void OnHide()
    {
        LogUtils.Log("卸载登录界面");
        EventBus.Unsubscribe<LoginResultEvent>(OnLoginResponse);
    }

    private void OnLoginButtonClicked()
    {

        // 获取用户名和密码
        string username = GetInputText("Username");
        string password = GetInputText("Password");

        // 执行登录逻辑
        LogUtils.Log($"Attempting login with Username: {username}, Password: {password}");

        GetMod<ModLogin>().Login(username, password);
    }

    private void OnLoginResponse(LoginResultEvent eventData)
    {
        if (eventData.IsSuccess)
        {
            // 切换到主页
            ShowCanvas(ViewNames.SELECT_PLAYER);
            HideCanvas();
        }
        else
        {
            SetText("Message", eventData.Message);
        }
    }

}