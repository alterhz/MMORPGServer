using System;

/// <summary>
/// 协议注解特性，用于标识协议类对应的协议ID
/// </summary>
[AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
public class Proto : Attribute
{
    /// <summary>
    /// 协议ID
    /// </summary>
    public int ProtocolId { get; private set; }

    /// <summary>
    /// 构造函数
    /// </summary>
    /// <param name="protocolId">协议ID</param>
    public Proto(int protocolId)
    {
        ProtocolId = protocolId;
    }
}