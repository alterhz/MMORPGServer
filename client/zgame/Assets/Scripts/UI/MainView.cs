using ZGame;

/// <summary>
/// 主界面管理器，负责主界面的UI逻辑
/// </summary>
public class MainView : ViewBase
{
    // 构造函数，初始化主界面管理器
    public MainView() : base(ViewNames.MAIN)
    {
    }

    public override void OnInitialize()
    {
        // 给返回按钮绑定事件
        AddButtonClickListener("GoBack", OnGoBackButtonClicked);
    }

    public override void OnShow()
    {
        LogUtils.Log("加载主界面");
    }

    public override void OnHide()
    {
        LogUtils.Log("卸载主界面");
    }

    private void OnGoBackButtonClicked()
    {
        // 返回到登录界面
        ShowCanvas(ViewNames.LOGIN);
    }
}