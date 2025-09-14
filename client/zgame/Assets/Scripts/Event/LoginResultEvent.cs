using System;

/// <summary>
/// 余额查询结果事件（逻辑层→UI层传递数据）
/// </summary>
public class LoginResultEvent : EventArgs
{
    public long Code { get; } // 是否查询成功
    public string Message { get; } // 提示信息（失败时有效）

    public LoginResultEvent(long code, string message)
    {
        Code = code;
        Message = message;
    }

    public bool IsSuccess => Code == 0;

    // 成功，code=0
    public static LoginResultEvent Success(string message)
    {
        return new LoginResultEvent(0, message);
    }

    // 失败，code非0
    public static LoginResultEvent Failure(int code, string message)
    {
        return new LoginResultEvent(code, message);
    }
    
}