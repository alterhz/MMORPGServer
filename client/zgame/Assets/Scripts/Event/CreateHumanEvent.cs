using System;

public class CreateHumanEvent : EventArgs
{
    public bool IsSuccess { get; }
    public string Message { get; }

    public CreateHumanEvent(bool isSuccess, string message)
    {
        IsSuccess = isSuccess;
        Message = message;
    }

    public static CreateHumanEvent Success(string message = "创建角色成功")
    {
        return new CreateHumanEvent(true, message);
    }

    public static CreateHumanEvent Failure(string message)
    {
        return new CreateHumanEvent(false, message);
    }
}