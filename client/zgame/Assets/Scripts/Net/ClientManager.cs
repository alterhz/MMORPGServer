using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class ClientManager : MonoBehaviour
{
    private NettyClient client;

    private static ClientManager _instance;

    public static ClientManager Instance
    {
        get
        {
            if (_instance == null)
            {
                _instance = FindObjectOfType<ClientManager>();
                if (_instance == null)
                {
                    GameObject go = new GameObject("ClientManager");
                    _instance = go.AddComponent<ClientManager>();
                }
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
        
        // 注册消息处理器（直接处理JSON字符串）
        // client.RegisterHandler(1002, (json) => {
        //     Debug.Log("收到协议2的消息: " + json);
        // });
        
        Debug.Log("初始化网络");
    }

    public void Connect()
    {
        // 连接到服务器
        client.ConnectToServer();
    }


    public void Login(string username, string password)
    {
        LoginRequest request = new LoginRequest();
        request.username = username;
        request.password = password;

        // 发送登录请求
        client.SendObject(1001, request);

        Debug.Log("发送登录请求。username: " + username + " password: " + password);
    }

    public void RegisterHandler<T>(int protocolId, Action<T> handler)
    {   
        client.RegisterHandler<T>(protocolId, handler);
    }

    // Update is called once per frame
    public void Run()
    {
        client.ProcessReceivedMessages();
    }
}