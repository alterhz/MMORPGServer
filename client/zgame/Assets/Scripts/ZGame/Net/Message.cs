using System;
using System.Text;

namespace ZGame
{
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

}
