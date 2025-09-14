using System;
using System.Collections.Generic;
using ZGame;

/// <summary>
/// 角色列表事件，用于通知View层更新角色列表UI
/// </summary>
public class PlayerListEvent : EventArgs
{
    public bool IsSuccess { get; }
    public List<Human> PlayerList { get; }
    public string Message { get; }

    public PlayerListEvent(bool isSuccess, List<Human> playerList, string message)
    {
        IsSuccess = isSuccess;
        PlayerList = playerList ?? new List<Human>();
        Message = message;
    }

    public static PlayerListEvent Success(List<Human> playerList, string message = "获取角色列表成功")
    {
        return new PlayerListEvent(true, playerList, message);
    }

    public static PlayerListEvent Failure(string message)
    {
        return new PlayerListEvent(false, null, message);
    }
}