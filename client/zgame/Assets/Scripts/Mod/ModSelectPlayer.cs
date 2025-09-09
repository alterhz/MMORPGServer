using System;
using System.Collections.Generic;
using ZGame;

public class ModSelectPlayer : ModBase
{
    public override void Initialize()
    {
        LogUtils.Log("ModSelectPlayer Initialized");
    }


    protected override void OnEnable()
    {
        // 注册协议处理器
        ClientManager.Instance.RegisterHandler(1004, OnQueryHumans);
        LogUtils.Log("ModSelectPlayer Enabled");
    }

    protected override void OnDisable()
    {
        // 注销协议处理器
        ClientManager.Instance.UnregisterHandler(1004, OnQueryHumans);
        LogUtils.Log("ModSelectPlayer Disabled");

    }

    /// <summary>
    /// 请求角色列表
    /// </summary>
    public void QueryHumans()
    {
        CSQueryHumans request = new CSQueryHumans();
        ClientManager.Instance.Send(1003, request);
    }

    /// <summary>
    /// 角色列表响应处理
    /// </summary>
    private void OnQueryHumans(Message message)
    {
        SCQueryHumans scQueryHumans = ProtoUtils.Deserialize<SCQueryHumans>(message.ToJson());
        
        if (scQueryHumans.code == 0)
        {
            LogUtils.Log("获取角色列表成功，角色数量: " + scQueryHumans.humanList.Count);
            // 在这里可以触发事件通知UI更新等操作
        }
        else
        {
            LogUtils.LogWarning("获取角色列表失败: " + scQueryHumans.message);
        }
    }
}