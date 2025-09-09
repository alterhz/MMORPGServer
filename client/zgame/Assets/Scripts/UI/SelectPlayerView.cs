using ZGame;

public class SelectPlayerView : ViewBase
{
    public SelectPlayerView() : base(ViewNames.SELECT_PLAYER)
    {
    }

    public override void OnInitialize()
    {
        LogUtils.Log("初始化选择角色界面");
        // 添加返回按钮点击事件监听
        AddButtonClickListener("Back", OnBackButtonClicked);
    }

    public override void OnShow()
    {
        // 监听事件
        EventBus.Subscribe<PlayerListEvent>(OnPlayerListEvent);
        LogUtils.Log("加载选择角色界面");
    }

    public override void OnHide()
    {
        EventBus.Unsubscribe<PlayerListEvent>(OnPlayerListEvent);
        LogUtils.Log("卸载选择角色界面");
    }
    
    // 返回按钮点击事件处理函数
    private void OnBackButtonClicked()
    {
        // 返回到登录界面
        ShowCanvas(ViewNames.LOGIN);
    }
    
    // 角色列表事件处理函数
    private void OnPlayerListEvent(PlayerListEvent e)
    {
        if (e.IsSuccess)
        {
            LogUtils.Log($"接收到角色列表，共有 {e.PlayerList.Count} 个角色");
            // 在这里处理角色列表UI更新逻辑
            // 例如：创建角色按钮、显示角色信息等
        }
        else
        {
            LogUtils.LogWarning($"获取角色列表失败: {e.Message}");
            // 在这里处理获取角色列表失败的UI提示逻辑
        }
    }
}