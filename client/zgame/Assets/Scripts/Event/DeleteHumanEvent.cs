// 新增删除角色事件类
using System;

public class DeleteHumanEvent : EventArgs
{
    public bool IsSuccess { get; private set; }
    public string HumanId { get; private set; }
    public string Message { get; private set; }

    private DeleteHumanEvent(bool isSuccess, string humanId, string message)
    {
        IsSuccess = isSuccess;
        HumanId = humanId;
        Message = message;
    }

    public static DeleteHumanEvent Success(string humanId, string message)
    {
        return new DeleteHumanEvent(true, humanId, message);
    }

    public static DeleteHumanEvent Failure(string humanId, string message)
    {
        return new DeleteHumanEvent(false, humanId, message);
    }
}