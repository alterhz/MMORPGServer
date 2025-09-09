using ZGame;

/// <summary>
/// 主界面管理器，负责主界面的UI逻辑
/// </summary>
public class MainView : ViewBase
{

    // 构造函数，初始化主界面管理器
    public MainView()
    {
        // 注册Canvas
        RegisterCanvas(false);

        // 给返回按钮绑定事件
        AddButtonClickListener("GoBack", OnGoBackButtonClicked);
    }

    private void OnGoBackButtonClicked()
    {
        // 获取UIManager单例
        UIManager uiManager = UIManager.Instance;

        // 返回到登录界面
        uiManager.ShowCanvas("Login");
    }
}