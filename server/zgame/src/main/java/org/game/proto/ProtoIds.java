package org.game.proto;

/**
 * 协议ID列表，定义不需要使用
 */

public class ProtoIds {

    public static final int CS_LOGIN = 1001;

    public static final int SC_LOGIN = 1002;

    // 请求角色列表
    public static final int CS_QUERY_HUMANS = 1003;

    public static final int SC_QUERY_HUMANS = 1004;

    // 选择角色
    public static final int CS_SELECT_HUMAN = 1005;

    public static final int SC_SELECT_HUMAN = 1006;

    // 创建角色
    public static final int CS_CREATE_HUMAN = 1007;

    public static final int SC_CREATE_HUMAN = 1008;

    // 删除角色
    public static final int CS_DELETE_HUMAN = 1009;

    public static final int SC_DELETE_HUMAN = 1010;

    // 发送客户端数据开始
    public static final int SC_SEND_TO_CLIENT_BEGIN = 1011;

    // 发送客户端数据结束
    public static final int SC_SEND_TO_CLIENT_END = 1012;

    // 测试
    public static final int CS_TEST = 1101;

    public static final int SC_TEST = 1102;
}
