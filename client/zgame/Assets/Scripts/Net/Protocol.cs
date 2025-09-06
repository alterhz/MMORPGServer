using System;
using System.Collections.Generic;

// 示例：定义消息数据类
[Serializable]
public class CSLogin
{
    public string account;
    public string password;
}

[Serializable]
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
public class CSQueryHumans 
{
}


[Serializable]
public class SCQueryHumans 
{
    public int code;
    public List<HumanInfo> humanList;
    public string message;
}

[Serializable]
public class CSSelectHuman 
{
    public string humanId;
}


[Serializable]
public class SCSelectHuman
{
    public int code;
    public string message;
}

[Serializable]
public class CSCreateHuman 
{
    public string name;
    public string profession;
}


[Serializable]
public class SCCreateHuman 
{
    public int code;
    public string humanId;
    public string message;
    public bool success;
}



[Serializable]
public class SCSendToClientBegin 
{
}

[Serializable]
public class SCSendToClientEnd 
{
}


[Serializable]
public class SCTest 
{
    public string content;
}

[Serializable]
public class CSTest 
{
}
