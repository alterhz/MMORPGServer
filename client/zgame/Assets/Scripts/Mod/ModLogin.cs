using System;
using ZGame;

/// <summary>
/// 登录模块，处理用户登录逻辑和监听登录消息返回
/// </summary>
public class ModLogin : ModBase
{
    public ModLogin()
    {

    }

    /// <summary>
    /// 初始化登录模块
    /// </summary>
    public override void Initialize()
    {
        LogUtils.Log("初始化登录模块");
    }

    protected override void OnEnable()
    {
        // 协议处理器会通过ProtoListener特性自动注册
    }

    protected override void OnDisable()
    {
        // 协议处理器会通过ProtoListener特性自动注销
    }

    /// <summary>
    /// 登录处理
    /// </summary>
    public void Login(string username, string password)
    {
        // EventManager.Instance.Trigger(new LoginResultEvent(0, "正在登录..."));

        // 连接服务器，并发送登录请求
        ClientManager.Instance.Connect();

        CsLogin request = new()
        {
            account = username,
            password = password
        };
        ClientManager.Instance.Send(request);
    }


    [ProtoListener]
    public void OnLogin(ScLogin scLogin)
    {
        EventManager.Instance.Trigger(new LoginResultEvent(scLogin.code, scLogin.message));

        if (scLogin.code == 0)
        {
            LogUtils.Log("登录成功，用户ID: " + scLogin.message);

            GetMod<ModSelectPlayer>().QueryPlayer();
        }
        else
        {
            LogUtils.LogWarning("登录失败: " + scLogin.message);
        }
    }


}