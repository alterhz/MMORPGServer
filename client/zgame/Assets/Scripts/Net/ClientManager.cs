using UnityEngine;
using System;
using ZGame;

public class ClientManager : Singleton<ClientManager>
{
    private NettyClient client;

    // Start is called before the first frame update
    public void InitClient()
    {
        client = new NettyClient();
    
        // 注册连接事件
        client.OnConnected += () => {
            Debug.Log("已连接到服务器");
        };
        
        // 注册错误处理
        client.OnError += (error) => {
            Debug.LogError("网络错误: " + error);
        };
        
        Debug.Log("初始化网络");
    }

    public void Connect()
    {
        // 连接到服务器
        client?.ConnectToServer();
    }
    
    public void Send<T>(int protocolId, T dataObject)
    {
        client?.SendObject(protocolId, dataObject);
    }

    public void RegisterHandler(int protocolId, Action<Message> handler)
    {
        client?.EventDispatcher.RegisterEvent(protocolId.ToString(), handler);
    }

    public void UnregisterHandler(int protocolId, Action<Message> handler)
    {
        client?.EventDispatcher.UnregisterEvent(protocolId.ToString(), handler);
    }

    // Update is called once per frame
    public void Run()
    {
        client?.ProcessReceivedMessages();
    }

    internal void Destroy()
    {
        client?.Disconnect();
        client = null;
    }
}