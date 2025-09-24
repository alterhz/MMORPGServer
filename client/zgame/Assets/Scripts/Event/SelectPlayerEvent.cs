using System;

public class SelectPlayerEvent : EventArgs
{
    public bool IsSuccess { get; }
    public long PlayerId { get; }
    public string Message { get; }

    public SelectPlayerEvent(bool isSuccess, long playerId, string message)
    {
        IsSuccess = isSuccess;
        PlayerId = playerId;
        Message = message;
    }

    public static SelectPlayerEvent Success(long playerId, string message = "选择角色成功")
    {
        return new SelectPlayerEvent(true, playerId, message);
    }

    public static SelectPlayerEvent Failure(long playerId, string message)
    {
        return new SelectPlayerEvent(false, playerId, message);
    }
}