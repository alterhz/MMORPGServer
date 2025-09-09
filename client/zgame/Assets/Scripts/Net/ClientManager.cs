using UnityEngine;
using System;
using ZGame;
using System.Reflection;

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
    
    public void Send<T>(T dataObject)
    {
        client?.SendObject(dataObject);
    }

    public void RegisterProto(Type protoType, MethodInfo handler)
    {
        int protoId = ProtoScanner.GetProtoID(protoType);
        if (protoId == -1)
        {
            LogUtils.LogError("RegisterHandler没有注册的协议: " + protoType);
            return;
        }
        client?.EventDispatcher.RegisterEvent(protoId.ToString(), handler);
    }

    public void UnregisterProto(Type protoType, MethodInfo handler)
    {
        int protoId = ProtoScanner.GetProtoID(protoType);
        if (protoId == -1)
        {
            LogUtils.LogError("UnregisterHandler没有注册的协议: " + protoType);
            return;
        }
        client?.EventDispatcher.UnregisterEvent(protoId.ToString(), handler);
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