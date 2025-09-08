using System;

/// <summary>
/// 余额查询结果事件（逻辑层→UI层传递数据）
/// </summary>
public class BalanceQueryResultEvent : EventArgs
{
    public bool IsSuccess { get; } // 是否查询成功
    public float Balance { get; }  // 余额（成功时有效）
    public string Message { get; } // 提示信息（失败时有效）

    public BalanceQueryResultEvent(bool isSuccess, float balance, string message)
    {
        IsSuccess = isSuccess;
        Balance = balance;
        Message = message;
    }
}