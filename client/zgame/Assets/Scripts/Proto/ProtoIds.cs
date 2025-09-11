using System;

/// <summary>
/// 协议ID列表，定义不需要使用
/// </summary>
public class ProtoIds 
{
    public const int CS_LOGIN = 1001;
    public const int SC_LOGIN = 1002;

    // 请求角色列表
    public const int CS_QUERY_HUMANS = 1003;
    public const int SC_QUERY_HUMANS = 1004;

    // 选择角色
    public const int CS_SELECT_HUMAN = 1005;
    public const int SC_SELECT_HUMAN = 1006;
    
    // 创建角色
    public const int CS_CREATE_HUMAN = 1007;
    public const int SC_CREATE_HUMAN = 1008;

    // 删除角色
    public const int CS_DELETE_HUMAN = 1009;
    public const int SC_DELETE_HUMAN = 1010;

    // 发送客户端数据开始
    public const int SC_SEND_TO_CLIENT_BEGIN = 1011;
    // 发送客户端数据结束
    public const int SC_SEND_TO_CLIENT_END = 1012;

    // 测试
    public const int CS_TEST = 1101;
    public const int SC_TEST = 1102;
}