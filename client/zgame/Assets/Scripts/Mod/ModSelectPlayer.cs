using System;
using System.Collections.Generic;
using ZGame;

public class ModSelectPlayer : ModBase
{
    private readonly List<HumanInfo> _playerList = new();

    public override void Initialize()
    {
        LogUtils.Log("ModSelectPlayer Initialized");

    }


    protected override void OnEnable()
    {
        // 注册协议处理器
        LogUtils.Log("ModSelectPlayer Enabled");
    }

    protected override void OnDisable()
    {
        // 注销协议处理器
        LogUtils.Log("ModSelectPlayer Disabled");
    }

    /// <summary>
    /// 请求角色列表
    /// </summary>
    public void QueryHumans()
    {
        CSQueryHumans request = new();
        ClientManager.Instance.Send(request);
    }

    [ProtoListener]
    public void OnLogin(SCLogin scLogin)
    {
        LogUtils.Log($"ModSelectPlayer 收到登录响应:{scLogin}");
    }


    /// <summary>
    /// 角色列表响应处理
    /// </summary>
    [ProtoListener]
    private void OnQueryHumans(SCQueryHumans scQueryHumans)
    {
        if (scQueryHumans.code == 0)
        {
            _playerList.Clear();
            _playerList.AddRange(scQueryHumans.humanList);

            LogUtils.Log("获取角色列表成功，角色数量: " + scQueryHumans.humanList.Count);
            // 触发角色列表事件
            EventBus.Trigger(PlayerListEvent.Success(scQueryHumans.humanList, scQueryHumans.message));
        }
        else
        {
            LogUtils.LogWarning("获取角色列表失败: " + scQueryHumans.message);
            // 触发角色列表事件（失败）
            EventBus.Trigger(PlayerListEvent.Failure(scQueryHumans.message));
        }
    }
    
    public List<HumanInfo> GetPlayerList()
    {
        return new List<HumanInfo>(_playerList);
    }
}