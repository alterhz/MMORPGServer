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

        AddButtonClickListener("CreateRole/Close", () =>
        {
            HideComponent("CreateRole");
        });
        
        // 添加创建角色按钮点击事件监听
        AddButtonClickListener("CreateRole/OK", OnCreateRoleOKButtonClicked);
        
        // 添加角色按钮点击事件监听
        AddButtonClickListener("Player1", () => OnPlayerButtonClicked(0));
        AddButtonClickListener("Player2", () => OnPlayerButtonClicked(1));
        AddButtonClickListener("Player3", () => OnPlayerButtonClicked(2));
        AddButtonClickListener("Player4", () => OnPlayerButtonClicked(3));
    }

    public override void OnShow()
    {
        HideComponent("CreateRole");
        SetText("Tips", "");
        SetText("CreateRole/Tips", "");
        // 清空输入框
        SetInputText("CreateRole/Name", "");

        // 监听事件
        EventBus.Subscribe<PlayerListEvent>(OnPlayerListEvent);
        EventBus.Subscribe<SelectHumanEvent>(OnSelectHumanEvent);
        EventBus.Subscribe<CreateHumanEvent>(OnCreateHumanEvent);
        LogUtils.Log("加载选择角色界面");
    }

    public override void OnHide()
    {
        EventBus.Unsubscribe<PlayerListEvent>(OnPlayerListEvent);
        EventBus.Unsubscribe<SelectHumanEvent>(OnSelectHumanEvent);
        EventBus.Unsubscribe<CreateHumanEvent>(OnCreateHumanEvent);
        LogUtils.Log("卸载选择角色界面");
    }
    
    // 返回按钮点击事件处理函数
    private void OnBackButtonClicked()
    {
        // 返回到登录界面
        ShowCanvas(ViewNames.LOGIN);
    }

    
    // 创建角色确认按钮点击事件处理函数
    private void OnCreateRoleOKButtonClicked()
    {
        string name = GetInputText("CreateRole/Name");
        string profession = "战士"; // 默认职业为战士，可根据需要扩展职业选择
        
        if (string.IsNullOrEmpty(name))
        {
            SetText("CreateRole/Tips", "角色名不能为空");
            return;
        }
        
        // 发送创建角色请求
        GetMod<ModSelectPlayer>().CreateHuman(name, profession);
    }
    
    // 角色按钮点击事件处理函数
    private void OnPlayerButtonClicked(int index)
    {
        // 获取当前角色列表
        var playerList = GetMod<ModSelectPlayer>().GetPlayerList();

        // 检查索引是否有效
        if (index >= 0 && index < playerList.Count)
        {
            // 获取选中的角色ID
            string humanId = playerList[index].id;

            // 请求选择角色
            GetMod<ModSelectPlayer>().SelectHuman(humanId);
        }
        else
        {
            LogUtils.LogWarning($"无效的角色索引: {index}");
            ShowComponent("CreateRole");
            // 清空Message文本
            SetText("Message", "");
        }
    }
    
    // 角色列表事件处理函数
    private void OnPlayerListEvent(PlayerListEvent playerListEvent)
    {
        if (playerListEvent.IsSuccess)
        {
            // 设置Player1-4按钮内容都为空
            for (int i = 0; i < 4; i++)
            {
                SetButtonText($"Player{i + 1}", "空");
            }

            var playerList = GetMod<ModSelectPlayer>().GetPlayerList();
            // 遍历角色列表，将角色信息显示在按钮上
            for (int i = 0; i < playerList.Count; i++)
            {
                HumanInfo humanInfo = playerList[i];
                SetButtonText($"Player{i + 1}", $"{humanInfo.name}");
            }

            LogUtils.Log($"接收到角色列表，共有 {playerList.Count} 个角色");
            // 在这里处理角色列表UI更新逻辑
            // 例如：创建角色按钮、显示角色信息等
        }
        else
        {
            LogUtils.LogWarning($"获取角色列表失败: {playerListEvent.Message}");
            // 在这里处理获取角色列表失败的UI提示逻辑
        }
    }
    
    // 选择角色事件处理函数
    private void OnSelectHumanEvent(SelectHumanEvent selectHumanEvent)
    {
        if (selectHumanEvent.IsSuccess)
        {
            LogUtils.Log($"选择角色成功: {selectHumanEvent.HumanId}");
            // 可以在这里添加进入游戏场景的逻辑
            // 关闭选择角色界面，打开游戏主界面
            ShowCanvas(ViewNames.MAIN);
            HideCanvas();
        }
        else
        {
            LogUtils.LogWarning($"选择角色失败: {selectHumanEvent.Message}");
            // 可以在这里添加错误提示逻辑
            SetText("Tips", selectHumanEvent.Message);
        }
    }
    
    // 创建角色事件处理函数
    private void OnCreateHumanEvent(CreateHumanEvent createHumanEvent)
    {
        // 隐藏创建角色面板
        HideComponent("CreateRole");
        
        if (createHumanEvent.IsSuccess)
        {
            LogUtils.Log("创建角色成功");
            // 显示成功消息
            SetText("Tips", "创建角色成功");
                        // 关闭选择角色界面，打开游戏主界面
            ShowCanvas(ViewNames.MAIN);
            HideCanvas();
        }
        else
        {
            LogUtils.LogWarning($"创建角色失败: {createHumanEvent.Message}");
            // 显示失败消息
            SetText("Tips", createHumanEvent.Message);
        }
    }
}