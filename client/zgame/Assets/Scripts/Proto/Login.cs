using System;
using System.Collections.Generic;

// 示例：定义消息数据类
[Serializable]
[Proto(ProtoIds.CS_LOGIN)]
public class CSLogin
{
    public string account;
    public string password;
}

[Serializable]
[Proto(ProtoIds.SC_LOGIN)]
public class SCLogin 
{
    public int code;
    public string message;
}

[Serializable]
public class HumanInfo 
{
    public string id;
    public string name;
    public string profession;
}

[Serializable]
[Proto(ProtoIds.CS_QUERY_HUMANS)]
public class CSQueryHumans 
{
}


[Serializable]
[Proto(ProtoIds.SC_QUERY_HUMANS)]
public class SCQueryHumans 
{
    public int code;
    public List<HumanInfo> humanList;
    public string message;
}

[Serializable]
[Proto(ProtoIds.CS_SELECT_HUMAN)]
public class CSSelectHuman 
{
    public string humanId;
}


[Serializable]
[Proto(ProtoIds.SC_SELECT_HUMAN)]
public class SCSelectHuman
{
    public int code;
    public string message;
}

[Serializable]
[Proto(ProtoIds.CS_CREATE_HUMAN)]
public class CSCreateHuman 
{
    public string name;
    public string profession;
}


[Serializable]
[Proto(ProtoIds.SC_CREATE_HUMAN)]
public class SCCreateHuman 
{
    public int code;
    public string humanId;
    public string message;
    public bool success;
}

[Serializable]
[Proto(ProtoIds.CS_DELETE_HUMAN)]
public class CSDeleteHuman
{
    public string humanId;
}

[Serializable]
[Proto(ProtoIds.SC_DELETE_HUMAN)]
public class SCDeleteHuman
{
    public int code;

    public string humanId;

    public string message;
}


[Serializable]
[Proto(ProtoIds.SC_SEND_TO_CLIENT_BEGIN)]
public class SCSendToClientBegin 
{
}

[Serializable]
[Proto(ProtoIds.SC_SEND_TO_CLIENT_END)]
public class SCSendToClientEnd 
{
}


[Serializable]
[Proto(ProtoIds.SC_TEST)]
public class SCTest 
{
    public string content;
}

[Serializable]
[Proto(ProtoIds.CS_TEST)]
public class CSTest 
{
}