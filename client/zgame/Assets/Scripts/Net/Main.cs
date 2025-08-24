using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Main : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        NettyClient client = GetComponent<NettyClient>();
    
        // 注册连接事件
        client.OnConnected += () => {
            Debug.Log("已连接到服务器");
        };
        
        // 注册错误处理
        client.OnError += (error) => {
            Debug.LogError("网络错误: " + error);
        };
        
        // 注册消息处理器（直接处理JSON字符串）
        client.RegisterHandler(1002, (json) => {
            Debug.Log("收到协议2的消息: " + json);
        });
        
        // 注册消息处理器（自动反序列化为对象）
        client.RegisterHandler<LoginResponse>(1002, (response) => {
            if (response.code == 0)
            {
                Debug.Log("登录成功，用户ID: " + response.data);
            }
            else
            {
                Debug.LogWarning("登录失败: " + response.message);
            }
        });
        
        // 连接到服务器
        client.ConnectToServer();

        // 方法2：发送对象（自动转换为JSON）
        LoginRequest loginRequest = new LoginRequest {
            username = "admin",
            password = "admin"
        };
        client.SendObject(1001, loginRequest);

        Debug.Log("已发送登录请求");
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
