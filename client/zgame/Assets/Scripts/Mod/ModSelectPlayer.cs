using System;
using System.Collections.Generic;
using ZGame;

public class ModSelectPlayer : ModBase
{
    private readonly List<Player> _playerList = new();

    private long selectedHumanId;

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
    public void QueryPlayer()
    {
        CsQueryPlayer request = new();
        Send(request);
    }

    /// <summary>
    /// 角色列表响应处理
    /// </summary>
    [ProtoListener]
    private void OnQueryPlayer(ScQueryPlayer scQueryHuman)
    {
        if (scQueryHuman.code == 0)
        {
            _playerList.Clear();
            _playerList.AddRange(scQueryHuman.player);

            LogUtils.Log("获取角色列表成功，角色数量: " + scQueryHuman.player.Count);
            // 触发角色列表事件
            EventManager.Instance.Trigger(PlayerListEvent.Success(scQueryHuman.player, scQueryHuman.message));
        }
        else
        {
            LogUtils.LogWarning("获取角色列表失败: " + scQueryHuman.message);
            // 触发角色列表事件（失败）
            EventManager.Instance.Trigger(PlayerListEvent.Failure(scQueryHuman.message));
        }
    }

    /// <summary>
    /// 选择角色进入游戏
    /// </summary>
    /// <param name="playerId">角色ID</param>
    public void SelectPlayer(long playerId)
    {
        CsSelectPlayer request = new()
        {
            playerId = playerId
        };
        Send(request);
        selectedHumanId = playerId;
        LogUtils.Log("请求选择角色: " + playerId);
    }

    /// <summary>
    /// 选择角色响应处理
    /// </summary>
    [ProtoListener]
    private void OnSelectPlayer(ScSelectPlayer scSelectPlayer)
    {
        if (scSelectPlayer.code == 0)
        {
            LogUtils.Log("选择角色成功");
            // 触发选择角色事件（成功）
            EventManager.Instance.Trigger(SelectPlayerEvent.Success(selectedHumanId, scSelectPlayer.message));
        }
        else
        {
            LogUtils.LogWarning("选择角色失败: " + scSelectPlayer.message);
            // 触发选择角色事件（失败）
            EventManager.Instance.Trigger(SelectPlayerEvent.Failure(selectedHumanId, scSelectPlayer.message));
        }
    }


    public List<Player> GetPlayerList()
    {
        return new List<Player>(_playerList);
    }

    /// <summary>
    /// 创建角色
    /// </summary>
    /// <param name="name">角色名</param>
    /// <param name="profession">职业</param>
    public void CreatePlayer(string name, string profession)
    {
        CsCreatePlayer request = new()
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
    private void OnCreatePlayer(ScCreatePlayer scCreateHuman)
    {
        if (scCreateHuman.code == 0)
        {
            LogUtils.Log("创建角色成功");
            // 触发创建角色成功事件
            EventManager.Instance.Trigger(CreatePlayerEvent.Success(scCreateHuman.message));
        }
        else
        {
            LogUtils.LogWarning("创建角色失败: " + scCreateHuman.message);
            // 触发创建角色失败事件
            EventManager.Instance.Trigger(CreatePlayerEvent.Failure(scCreateHuman.message));
        }
    }
    
    /// <summary>
    /// 删除角色
    /// </summary>
    /// <param name="playerId">角色ID</param>
    public void DeletePlayer(long playerId)
    {
        CsDeletePlayer request = new()
        {
            playerId = playerId
        };
        Send(request);
        LogUtils.Log($"请求删除角色: {playerId}");
    }

    /// <summary>
    /// 删除角色响应处理
    /// </summary>
    [ProtoListener]
    private void OnDeletePlayer(ScDeletePlayer scDeletePlayer)
    {
        if (scDeletePlayer.code == 0)
        {
            LogUtils.Log($"删除角色成功{scDeletePlayer.playerId}");
            _playerList.RemoveAll(h => h.id == scDeletePlayer.playerId);

            // 触发删除角色成功事件
            EventManager.Instance.Trigger(DeletePlayerEvent.Success(scDeletePlayer.playerId, scDeletePlayer.message));
        }
        else
        {
            LogUtils.LogWarning("删除角色失败: " + scDeletePlayer.message);
            // 触发删除角色失败事件
            EventManager.Instance.Trigger(DeletePlayerEvent.Failure(scDeletePlayer.playerId, scDeletePlayer.message));
        }
    }

}