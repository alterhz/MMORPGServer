// 新增删除角色事件类
using System;

public class DeletePlayerEvent : EventArgs
{
    public bool IsSuccess { get; private set; }
    public long PlayerId { get; private set; }
    public string Message { get; private set; }

    private DeletePlayerEvent(bool isSuccess, long playerId, string message)
    {
        IsSuccess = isSuccess;
        PlayerId = playerId;
        Message = message;
    }

    public static DeletePlayerEvent Success(long playerId, string message)
    {
        return new DeletePlayerEvent(true, playerId, message);
    }

    public static DeletePlayerEvent Failure(long playerId, string message)
    {
        return new DeletePlayerEvent(false, playerId, message);
    }
}