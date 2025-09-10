using System;
using System.Collections.Generic;
using ZGame;

public class ModSelectPlayer : ModBase
{
    private readonly List<HumanInfo> _playerList = new();

    private string selectedHumanId;

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
        Send(request);
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

    /// <summary>
    /// 选择角色进入游戏
    /// </summary>
    /// <param name="humanId">角色ID</param>
    public void SelectHuman(string humanId)
    {
        CSSelectHuman request = new();
        request.humanId = humanId;
        Send(request);
        selectedHumanId = humanId;
        LogUtils.Log("请求选择角色: " + humanId);
    }

    /// <summary>
    /// 选择角色响应处理
    /// </summary>
    [ProtoListener]
    private void OnSelectHuman(SCSelectHuman scSelectHuman)
    {
        if (scSelectHuman.code == 0)
        {
            LogUtils.Log("选择角色成功");
            // 触发选择角色事件（成功）
            EventBus.Trigger(SelectHumanEvent.Success(selectedHumanId, scSelectHuman.message));
        }
        else
        {
            LogUtils.LogWarning("选择角色失败: " + scSelectHuman.message);
            // 触发选择角色事件（失败）
            EventBus.Trigger(SelectHumanEvent.Failure(selectedHumanId, scSelectHuman.message));
        }
    }

    
    public List<HumanInfo> GetPlayerList()
    {
        return new List<HumanInfo>(_playerList);
    }

    /// <summary>
    /// 创建角色
    /// </summary>
    /// <param name="name">角色名</param>
    /// <param name="profession">职业</param>
    public void CreateHuman(string name, string profession)
    {
        CSCreateHuman request = new()
        {
            name = name,
            profession = profession
        };
        Send(request);
        LogUtils.Log($"请求创建角色: {name}, 职业: {profession}");
    }

    /// <summary>
    /// 创建角色响应处理
    /// </summary>
    [ProtoListener]
    private void OnCreateHuman(SCCreateHuman scCreateHuman)
    {
        if (scCreateHuman.code == 0)
        {
            LogUtils.Log("创建角色成功");
            // 触发创建角色成功事件
            EventBus.Trigger(CreateHumanEvent.Success(scCreateHuman.message));
        }
        else
        {
            LogUtils.LogWarning("创建角色失败: " + scCreateHuman.message);
            // 触发创建角色失败事件
            EventBus.Trigger(CreateHumanEvent.Failure(scCreateHuman.message));
        }
    }
}