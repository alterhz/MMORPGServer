using System;

public class SelectHumanEvent : EventArgs
{
    public bool IsSuccess { get; }
    public string HumanId { get; }
    public string Message { get; }

    public SelectHumanEvent(bool isSuccess, string humanId, string message)
    {
        IsSuccess = isSuccess;
        HumanId = humanId;
        Message = message;
    }

    public static SelectHumanEvent Success(string humanId, string message = "选择角色成功")
    {
        return new SelectHumanEvent(true, humanId, message);
    }

    public static SelectHumanEvent Failure(string humanId, string message)
    {
        return new SelectHumanEvent(false, humanId, message);
    }
}