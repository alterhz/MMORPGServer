using UnityEngine;
using System;

public class ClientManager
{
    private NettyClient client;

    private static ClientManager _instance;

    public static ClientManager Instance
    {
        get
        {
            if (_instance == null)
            {
                _instance = new ClientManager();
            }
            return _instance;
        }
    }

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
        client.ConnectToServer();
    }
    
    public void Send<T>(int protocolId, T dataObject)
    {
        client.SendObject(protocolId, dataObject);
    }

    public void RegisterHandler(int protocolId, Action<Message> handler)
    {
        client?.EventDispatcher.RegisterEvent(protocolId.ToString(), handler);
    }

    // Update is called once per frame
    public void Run()
    {
        client?.ProcessReceivedMessages();
    }
}