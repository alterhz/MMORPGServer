public class MainView
{
    private const string CanvasName = "Main";
    private const string CanvasPath = "Main";
    
    // 构造函数，初始化主界面管理器
    public MainView()
    {
        // 获取UIManager单例
        UIManager uiManager = UIManager.Instance;
        
        // 注册Canvas
        uiManager.RegisterCanvas(CanvasPath, false);
        
        // 给返回按钮绑定事件
        uiManager.AddButtonClickListener(CanvasName, "GoBack", OnGoBackButtonClicked);
    }

    private void OnGoBackButtonClicked()
    {
        // 获取UIManager单例
        UIManager uiManager = UIManager.Instance;
        
        // 返回到登录界面
        uiManager.ShowCanvas("Login");
    }
}