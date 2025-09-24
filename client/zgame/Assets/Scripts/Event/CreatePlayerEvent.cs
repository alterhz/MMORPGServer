using System;

public class CreatePlayerEvent : EventArgs
{
    public bool IsSuccess { get; }
    public string Message { get; }

    public CreatePlayerEvent(bool isSuccess, string message)
    {
        IsSuccess = isSuccess;
        Message = message;
    }

    public static CreatePlayerEvent Success(string message = "创建角色成功")
    {
        return new CreatePlayerEvent(true, message);
    }

    public static CreatePlayerEvent Failure(string message)
    {
        return new CreatePlayerEvent(false, message);
    }
}