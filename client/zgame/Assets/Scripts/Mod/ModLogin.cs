using System;
using ZGame;

/// <summary>
/// 登录模块，处理用户登录逻辑和监听登录消息返回
/// </summary>
public class ModLogin : ModBase
{
    public ModLogin()
    {
        // 初始化模块
        Initialize();
    }

    /// <summary>
    /// 初始化登录模块
    /// </summary>
    protected override void Initialize()
    {
        // 注册登录协议处理器
        ClientManager.Instance.RegisterHandler(1002, OnLogin);
    }

    /// <summary>
    /// 登录处理
    /// </summary>
    public void Login(string username, string password)
    {            
        // 连接服务器，并发送登录请求
        ClientManager.Instance.Connect();

        CSLogin request = new()
        {
            account = username,
            password = password
        };
        ClientManager.Instance.Send(1001, request);
    }


    public void OnLogin(Message message)
    {
        SCLogin scLogin = ProtoUtils.Deserialize<SCLogin>(message.ToJson());

        EventBus.Trigger(new LoginResultEvent(scLogin.code, scLogin.message));

        if (scLogin.code == 0)
        {
            LogUtils.Log("登录成功，用户ID: " + scLogin.message);
        }
        else
        {
            LogUtils.LogWarning("登录失败: " + scLogin.message);
        }
    }

}