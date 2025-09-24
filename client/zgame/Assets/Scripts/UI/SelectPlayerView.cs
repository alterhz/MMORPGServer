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

        // 添加角色按钮点击事件监听
        AddButtonClickListener("Player1/Delete", () => OnDeleteRoleButtonClicked(0));
        AddButtonClickListener("Player2/Delete", () => OnDeleteRoleButtonClicked(1));
        AddButtonClickListener("Player3/Delete", () => OnDeleteRoleButtonClicked(2));
        AddButtonClickListener("Player4/Delete", () => OnDeleteRoleButtonClicked(3));
    }

    protected override void OnShow()
    {
        HideComponent("CreateRole");
        SetText("Tips", "");
        SetText("CreateRole/Tips", "");
        // 清空输入框
        SetInputText("CreateRole/Name", "");

        LogUtils.Log("加载选择角色界面");
    }

    protected override void OnHide()
    {
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
        GetMod<ModSelectPlayer>().CreatePlayer(name, profession);
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
            long playerId = playerList[index].id;

            // 请求选择角色
            GetMod<ModSelectPlayer>().SelectPlayer(playerId);
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
    [EventListener]
    private void OnPlayerListEvent(PlayerListEvent playerListEvent)
    {
        if (playerListEvent.IsSuccess)
        {
            DisplayPlayerList();
        }
        else
        {
            LogUtils.LogWarning($"获取角色列表失败: {playerListEvent.Message}");
        }
    }

    private void DisplayPlayerList()
    {
        // 设置Player1-4按钮内容都为空
        for (int i = 0; i < 4; i++)
        {
            SetButtonText($"Player{i + 1}", "空");
            // 隐藏删除按钮
            HideComponent($"Player{i + 1}/Delete");
        }

        var playerList = GetMod<ModSelectPlayer>().GetPlayerList();
        // 遍历角色列表，将角色信息显示在按钮上
        for (int i = 0; i < playerList.Count; i++)
        {
            Player player = playerList[i];
            SetButtonText($"Player{i + 1}", $"{player.name}");
            // 显示删除按钮
            ShowComponent($"Player{i + 1}/Delete");
        }

        LogUtils.Log($"接收到角色列表，共有 {playerList.Count} 个角色");
    }

    // 选择角色事件处理函数
    [EventListener]
    private void OnSelectPlayerEvent(SelectPlayerEvent selectHumanEvent)
    {
        if (selectHumanEvent.IsSuccess)
        {
            LogUtils.Log($"选择角色成功: {selectHumanEvent.PlayerId}");
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
    [EventListener]
    private void OnCreatePlayerEvent(CreatePlayerEvent createHumanEvent)
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
    
    // 删除角色按钮点击事件处理函数
    private void OnDeleteRoleButtonClicked(int index)
    {
        var playerList = GetMod<ModSelectPlayer>().GetPlayerList();

        // 检查索引是否有效
        if (index >= 0 && index < playerList.Count)
        {
            // 获取选中的角色ID
            long playerId = playerList[index].id;
            // 角色名
            string humanName = playerList[index].name;

            ConfirmPanel.Create($"确定要删除该角色（{humanName}）吗？", () =>
            {
                LogUtils.Log($"确认删除角色: {humanName} (ID: {playerId})");
                // 请求删除角色
                GetMod<ModSelectPlayer>().DeletePlayer(playerId);
            }, null).Show();

            
        }
        else
        {
            LogUtils.LogWarning($"无效的角色索引: {index}");
        }
    }

    // 删除角色事件处理函数
    [EventListener]
    private void OnDeletePlayerEvent(DeletePlayerEvent deletePlayerEvent)
    {
        if (deletePlayerEvent.IsSuccess)
        {
            LogUtils.Log($"删除角色成功: {deletePlayerEvent.PlayerId}");
            SetText("Tips", "删除成功");
            DisplayPlayerList();
        }
        else
        {
            LogUtils.LogWarning($"删除角色失败: {deletePlayerEvent.Message}");
            SetText("Tips", deletePlayerEvent.Message);
        }
    }
}