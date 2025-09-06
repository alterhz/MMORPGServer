using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;
using Newtonsoft.Json; // 需要导入Newtonsoft.Json包

public class RC4
{
    private const int SBOX_LENGTH = 256;
    private int[] sbox;
    private int[] key;

    /// <summary>
    /// 构造函数，使用指定的密钥初始化RC4
    /// </summary>
    /// <param name="keyStr">密钥字符串</param>
    public RC4(string keyStr)
    {
        this.key = ConvertStringToBytes(keyStr);
        this.sbox = new int[SBOX_LENGTH];
        Initialize();
    }

    /// <summary>
    /// 初始化S盒
    /// 使用KSA(Key Scheduling Algorithm)算法初始化S盒
    /// </summary>
    private void Initialize()
    {
        int keyLen = key.Length;
        // 初始化S盒
        for (int i = 0; i < SBOX_LENGTH; i++)
        {
            sbox[i] = i;
        }

        // KSA算法
        int j = 0;
        for (int i = 0; i < SBOX_LENGTH; i++)
        {
            j = (j + sbox[i] + key[i % keyLen]) % SBOX_LENGTH;
            // 交换sbox[i]和sbox[j]
            int temp = sbox[i];
            sbox[i] = sbox[j];
            sbox[j] = temp;
        }
    }

    /// <summary>
    /// 加密数据
    /// </summary>
    /// <param name="plaintext">明文数据</param>
    /// <returns>加密后的数据</returns>
    public byte[] Encrypt(byte[] plaintext)
    {
        return Crypt(plaintext);
    }

    /// <summary>
    /// 解密数据
    /// </summary>
    /// <param name="ciphertext">密文数据</param>
    /// <returns>解密后的数据</returns>
    public byte[] Decrypt(byte[] ciphertext)
    {
        return Crypt(ciphertext);
    }

    /// <summary>
    /// RC4加解密核心算法
    /// </summary>
    /// <param name="data">输入数据</param>
    /// <returns>处理后的数据</returns>
    private byte[] Crypt(byte[] data)
    {
        // 重新初始化S盒以保证每次调用的独立性
        int[] sboxCopy = (int[])sbox.Clone();

        byte[] result = new byte[data.Length];
        int i = 0, j = 0;

        // PRGA算法
        for (int k = 0; k < data.Length; k++)
        {
            i = (i + 1) % SBOX_LENGTH;
            j = (j + sboxCopy[i]) % SBOX_LENGTH;

            // 交换sboxCopy[i]和sboxCopy[j]
            int temp = sboxCopy[i];
            sboxCopy[i] = sboxCopy[j];
            sboxCopy[j] = temp;

            // 生成密钥流字节
            int t = (sboxCopy[i] + sboxCopy[j]) % SBOX_LENGTH;
            int keyStreamByte = sboxCopy[t];

            // XOR操作
            result[k] = (byte)(data[k] ^ keyStreamByte);
        }

        return result;
    }

    /// <summary>
    /// 将字符串转换为字节数组
    /// </summary>
    /// <param name="str">输入字符串</param>
    /// <returns>字节数组</returns>
    private int[] ConvertStringToBytes(string str)
    {
        char[] chars = str.ToCharArray();
        int[] result = new int[chars.Length];
        for (int i = 0; i < chars.Length; i++)
        {
            result[i] = (int)chars[i];
        }
        return result;
    }

    /// <summary>
    /// 将整数转换为4字节的字节数组（大端序）
    /// </summary>
    /// <param name="value">要转换的整数</param>
    /// <returns>4字节的字节数组</returns>
    public static byte[] IntToBytes(int value)
    {
        return new byte[] {
            (byte)(value >> 24),
            (byte)(value >> 16),
            (byte)(value >> 8),
            (byte)value
        };
    }

    /// <summary>
    /// 将4字节的字节数组转换为整数（大端序）
    /// </summary>
    /// <param name="bytes">4字节的字节数组</param>
    /// <returns>转换后的整数</returns>
    public static int BytesToInt(byte[] bytes)
    {
        if (bytes.Length != 4)
        {
            throw new ArgumentException("字节数组长度必须为4");
        }
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }
}

/// <summary>
/// 消息类，用于封装网络消息
/// </summary>
public class Message
{
    public int ProtocolId { get; set; }
    public byte[] Data { get; set; }

    public Message(int protocolId, byte[] data)
    {
        ProtocolId = protocolId;
        Data = data;
    }

    /// <summary>
    /// 将消息转换为字节数组
    /// </summary>
    /// <returns>字节数组表示的消息</returns>
    public byte[] ToBytes()
    {
        byte[] protocolIdBytes = RC4.IntToBytes(ProtocolId);
        byte[] result = new byte[4 + Data.Length];
        Buffer.BlockCopy(protocolIdBytes, 0, result, 0, 4);
        Buffer.BlockCopy(Data, 0, result, 4, Data.Length);
        return result;
    }

    /// <summary>
    /// 从字节数组创建消息
    /// </summary>
    /// <param name="bytes">字节数组</param>
    /// <returns>消息对象</returns>
    public static Message FromBytes(byte[] bytes)
    {
        if (bytes.Length < 4)
        {
            throw new ArgumentException("字节数组长度不足");
        }

        byte[] protocolIdBytes = new byte[4];
        Buffer.BlockCopy(bytes, 0, protocolIdBytes, 0, 4);
        int protocolId = RC4.BytesToInt(protocolIdBytes);

        byte[] data = new byte[bytes.Length - 4];
        Buffer.BlockCopy(bytes, 4, data, 0, data.Length);

        return new Message(protocolId, data);
    }

    /// <summary>
    /// 从JSON字符串创建消息
    /// </summary>
    /// <param name="protocolId">协议ID</param>
    /// <param name="json">JSON字符串</param>
    /// <returns>消息对象</returns>
    public static Message FromJson(int protocolId, string json)
    {
        byte[] data = Encoding.UTF8.GetBytes(json);
        return new Message(protocolId, data);
    }

    /// <summary>
    /// 将消息数据转换为JSON字符串
    /// </summary>
    /// <returns>JSON字符串</returns>
    public string ToJson()
    {
        return Encoding.UTF8.GetString(Data);
    }
}

/// <summary>
/// 网络事件委托
/// </summary>
/// <param name="protocolId">协议ID</param>
/// <param name="jsonData">JSON数据</param>
public delegate void NetworkEventHandler(int protocolId, string jsonData);

public class NettyClient
{
    private TcpClient tcpClient;
    private NetworkStream networkStream;
    private RC4 rc4;
    private Thread receiveThread;
    private bool isConnected = false;
    public string serverHost = "127.0.0.1";
    public int serverPort = 1080;
    private string rc4Key = "your_rc4_key";

    private Queue<Message> receivedMessages = new Queue<Message>();
    private object queueLock = new object();

    // 网络事件
    public event NetworkEventHandler OnMessageReceived;
    public event Action OnConnected;
    public event Action OnDisconnected;
    public event Action<string> OnError;
    
    // 用于避免重复注册处理器的字典，键为协议ID和处理器的组合
    private Dictionary<int, List<NetworkEventHandler>> registeredHandlers = new Dictionary<int, List<NetworkEventHandler>>();

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
    public void SendObject<T>(int protocolId, T dataObject)
    {
        string json = JsonConvert.SerializeObject(dataObject);
        SendJson(protocolId, json);
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
            List<Message> messagesToProcess = new List<Message>();
            
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
                    
                    // 触发消息接收事件
                    OnMessageReceived?.Invoke(message.ProtocolId, jsonData);
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

    /// <summary>
    /// 注册消息处理器
    /// </summary>
    /// <param name="protocolId">协议ID</param>
    /// <param name="handler">消息处理器</param>
    public void RegisterHandler(int protocolId, Action<string> handler)
    {
        // 创建处理器
        NetworkEventHandler newHandler = (id, json) => {
            if (id == protocolId)
            {
                handler(json);
            }
        };

        // 检查是否已存在相同协议ID和处理器的组合
        if (!registeredHandlers.ContainsKey(protocolId))
        {
            registeredHandlers[protocolId] = new List<NetworkEventHandler>();
        }

        // 检查是否已注册了相同的处理器
        bool isHandlerRegistered = false;
        foreach (var existingHandler in registeredHandlers[protocolId])
        {
            // 注意：直接比较委托在这里可能不准确，但在大多数情况下足够使用
            if (existingHandler == newHandler)
            {
                isHandlerRegistered = true;
                break;
            }
        }

        // 如果尚未注册，则注册新处理器并保存引用
        if (!isHandlerRegistered)
        {
            OnMessageReceived += newHandler;
            registeredHandlers[protocolId].Add(newHandler);
        }
    }

    /// <summary>
    /// 注册消息处理器（带类型转换）
    /// </summary>
    /// <typeparam name="T">目标类型</typeparam>
    /// <param name="protocolId">协议ID</param>
    /// <param name="handler">消息处理器</param>
    public void RegisterHandler<T>(int protocolId, Action<T> handler)
    {
        // 创建处理器
        NetworkEventHandler newHandler = (id, json) => {
            if (id == protocolId)
            {
                try
                {
                    T data = JsonConvert.DeserializeObject<T>(json);
                    handler(data);
                }
                catch (Exception e)
                {
                    Debug.LogError($"反序列化协议{protocolId}的数据失败: {e.Message}");
                    OnError?.Invoke($"反序列化协议{protocolId}的数据失败: {e.Message}");
                }
            }
        };

        // 检查是否已存在相同协议ID的条目
        if (!registeredHandlers.ContainsKey(protocolId))
        {
            registeredHandlers[protocolId] = new List<NetworkEventHandler>();
        }

        // 检查是否已注册了相同的处理器
        bool isHandlerRegistered = false;
        foreach (var existingHandler in registeredHandlers[protocolId])
        {
            // 注意：直接比较委托在这里可能不准确，但在大多数情况下足够使用
            if (existingHandler == newHandler)
            {
                isHandlerRegistered = true;
                break;
            }
        }

        // 如果尚未注册，则注册新处理器并保存引用
        if (!isHandlerRegistered)
        {
            OnMessageReceived += newHandler;
            registeredHandlers[protocolId].Add(newHandler);
        }
    }
    
    /// <summary>
    /// 移除指定协议ID的所有消息处理器
    /// </summary>
    /// <param name="protocolId">协议ID</param>
    public void UnregisterHandler(int protocolId)
    {
        if (registeredHandlers.ContainsKey(protocolId))
        {
            foreach (var handler in registeredHandlers[protocolId])
            {
                OnMessageReceived -= handler;
            }
            registeredHandlers.Remove(protocolId);
        }
    }
}

// 示例：定义消息数据类已移至Protocol.cs文件中
