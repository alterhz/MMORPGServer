using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Threading;
using UnityEngine;
using Newtonsoft.Json; // 需要导入Newtonsoft.Json包


namespace ZGame
{
    /// <summary>
    /// 基于TCP的Netty客户端，支持RC4加密和JSON消息格式
    /// </summary>

    public class NettyClient
    {
        private TcpClient tcpClient;
        private NetworkStream networkStream;
        private RC4 rc4;
        private Thread receiveThread;
        private bool isConnected = false;
        public string serverHost = "127.0.0.1";
        public int serverPort = 11001;
        private readonly string rc4Key = "your_rc4_key";

        private readonly Queue<Message> receivedMessages = new();
        private readonly object queueLock = new();

        // 网络事件
        public MethodDispatcher EventDispatcher = new();
        public event Action OnConnected;
        public event Action OnDisconnected;
        public event Action<string> OnError;


        private const int MAX_MESSAGES_PER_FRAME = 50; // 每帧处理的最大消息数

        /// <summary>
        /// 连接到服务器
        /// </summary>
        public void ConnectToServer()
        {
            try
            {
                tcpClient = new TcpClient();
                tcpClient.Connect(serverHost, serverPort);
                networkStream = tcpClient.GetStream();
                rc4 = new RC4(rc4Key);

                isConnected = true;

                // 启动接收线程
                receiveThread = new Thread(new ThreadStart(ReceiveData));
                receiveThread.IsBackground = true;
                receiveThread.Start();

                Debug.Log("成功连接到服务器: " + serverHost + ":" + serverPort);
                OnConnected?.Invoke();
            }
            catch (Exception e)
            {
                Debug.LogError("连接服务器失败: " + e.Message);
                OnError?.Invoke("连接服务器失败: " + e.Message);
            }
        }

        /// <summary>
        /// 断开与服务器的连接
        /// </summary>
        public void Disconnect()
        {
            isConnected = false;

            if (receiveThread != null && receiveThread.IsAlive)
            {
                receiveThread.Abort();
            }

            if (networkStream != null)
            {
                networkStream.Close();
            }

            if (tcpClient != null)
            {
                tcpClient.Close();
            }

            Debug.Log("已断开与服务器的连接");
            OnDisconnected?.Invoke();
        }

        /// <summary>
        /// 发送JSON消息到服务器
        /// </summary>
        /// <param name="protocolId">协议ID</param>
        /// <param name="jsonData">JSON数据</param>
        public void SendJson(int protocolId, string jsonData)
        {
            if (!isConnected)
            {
                Debug.LogWarning("未连接到服务器，无法发送消息");
                OnError?.Invoke("未连接到服务器，无法发送消息");
                return;
            }

            try
            {
                // 创建消息
                Message message = Message.FromJson(protocolId, jsonData);
                byte[] rawData = message.ToBytes();

                // 只加密协议ID和内容，不加密长度字段
                byte[] encryptedData = rc4.Encrypt(rawData);

                // 添加长度字段（未加密）
                byte[] lengthBytes = RC4.IntToBytes(encryptedData.Length);
                byte[] finalData = new byte[4 + encryptedData.Length];
                Buffer.BlockCopy(lengthBytes, 0, finalData, 0, 4);
                Buffer.BlockCopy(encryptedData, 0, finalData, 4, encryptedData.Length);

                // 发送数据
                networkStream.Write(finalData, 0, finalData.Length);
                networkStream.Flush();

                Debug.Log("已发送JSON消息，协议ID: " + protocolId + ", 数据: " + jsonData);
            }
            catch (Exception e)
            {
                Debug.LogError("发送消息失败: " + e.Message);
                OnError?.Invoke("发送消息失败: " + e.Message);
                Disconnect();
            }
        }

        /// <summary>
        /// 发送对象到服务器（自动转换为JSON）
        /// </summary>
        /// <typeparam name="T">对象类型</typeparam>
        /// <param name="protocolId">协议ID</param>
        /// <param name="dataObject">数据对象</param>
        public void SendObject<T>(T dataObject)
        {
            int protoId = ProtoScanner.GetProtoID(typeof(T));
            if (protoId == -1)
            {
                LogUtils.LogError("没有注册的协议: " + typeof(T).Name);
                return;
            }

            string json = JsonConvert.SerializeObject(dataObject);
            SendJson(protoId, json);
        }

        private void ReceiveData()
        {
            byte[] lengthBuffer = new byte[4];

            while (isConnected)
            {
                try
                {
                    // 读取长度字段（未加密）
                    int bytesRead = networkStream.Read(lengthBuffer, 0, 4);
                    if (bytesRead != 4)
                    {
                        Debug.LogError("读取长度字段失败");
                        OnError?.Invoke("读取长度字段失败");
                        Disconnect();
                        break;
                    }

                    // 解析长度字段
                    int messageLength = RC4.BytesToInt(lengthBuffer);

                    Debug.Log("已接收到消息，长度: " + messageLength);

                    // 读取加密的消息体
                    byte[] encryptedMessage = new byte[messageLength];
                    int totalRead = 0;
                    while (totalRead < messageLength)
                    {
                        bytesRead = networkStream.Read(encryptedMessage, totalRead, messageLength - totalRead);
                        if (bytesRead <= 0)
                        {
                            Debug.LogError("读取消息体失败");
                            OnError?.Invoke("读取消息体失败");
                            Disconnect();
                            break;
                        }
                        totalRead += bytesRead;
                    }

                    // 解密消息体
                    byte[] decryptedMessage = rc4.Decrypt(encryptedMessage);

                    // 解析消息
                    Message message = Message.FromBytes(decryptedMessage);

                    // 将消息加入队列，在主线程中处理
                    lock (queueLock)
                    {
                        receivedMessages.Enqueue(message);
                    }
                }
                catch (Exception e)
                {
                    if (isConnected)
                    {
                        Debug.LogError("接收数据时发生错误: " + e.Message);
                        OnError?.Invoke("接收数据时发生错误: " + e.Message);
                        Disconnect();
                    }
                    break;
                }
            }
        }

        /// <summary>
        /// 处理接收到的消息
        /// </summary>
        public void ProcessReceivedMessages()
        {
            int processedCount = 0;

            while (processedCount < MAX_MESSAGES_PER_FRAME)
            {
                // 创建一个临时列表来存储需要处理的消息
                List<Message> messagesToProcess = new();

                // 快速获取队列中的一批消息
                lock (queueLock)
                {
                    int remaining = MAX_MESSAGES_PER_FRAME - processedCount;
                    int count = Math.Min(remaining, receivedMessages.Count);
                    for (int i = 0; i < count; i++)
                    {
                        messagesToProcess.Add(receivedMessages.Dequeue());
                    }
                }

                // 如果没有消息需要处理，退出循环
                if (messagesToProcess.Count == 0)
                    break;

                // 处理这批消息
                foreach (Message message in messagesToProcess)
                {
                    try
                    {
                        string jsonData = message.ToJson();
                        Debug.Log("收到JSON消息，协议ID: " + message.ProtocolId + ", 数据: " + jsonData);

                        Type protoType = ProtoScanner.GetProtoClass(message.ProtocolId);

                        object proto = JsonConvert.DeserializeObject(jsonData, protoType);

                        // 触发消息接收事件
                        EventDispatcher.InvokeMethod("" + message.ProtocolId, proto);
                    }
                    catch (Exception e)
                    {
                        Debug.LogError("解析消息失败: " + e.Message);
                        OnError?.Invoke("解析消息失败: " + e.Message);
                    }

                    processedCount++;
                }
            }
        }

    }

    // 示例：定义消息数据类已移至Protocol.cs文件中
}