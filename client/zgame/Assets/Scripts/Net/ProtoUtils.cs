using System;
using Newtonsoft.Json;
using UnityEngine;

/// <summary>
/// 协议数据序列化和反序列化工具类
/// </summary>
public static class ProtoUtils
{
    /// <summary>
    /// 反序列化JSON数据为指定类型对象
    /// </summary>
    /// <typeparam name="T">目标类型</typeparam>
    /// <param name="json">JSON字符串</param>
    /// <param name="protocolId">协议ID（用于错误日志）</param>
    /// <param name="handler">处理反序列化数据的回调函数</param>
    /// <param name="onError">错误处理回调函数</param>
    public static void DeserializeAndHandle<T>(string json, int protocolId, Action<T> handler, Action<string> onError)
    {
        try
        {
            T data = JsonConvert.DeserializeObject<T>(json);
            handler(data);
        }
        catch (Exception e)
        {
            string errorMsg = $"反序列化协议{protocolId}的数据失败: {e.Message}";
            Debug.LogError(errorMsg);
            onError?.Invoke(errorMsg);
        }
    }
    
    /// <summary>
    /// 序列化对象为JSON字符串
    /// </summary>
    /// <param name="obj">要序列化的对象</param>
    /// <returns>JSON字符串</returns>
    public static string Serialize(object obj)
    {
        try
        {
            return JsonConvert.SerializeObject(obj);
        }
        catch (Exception e)
        {
            Debug.LogError($"序列化对象失败: {e.Message}");
            return null;
        }
    }
    
    /// <summary>
    /// 反序列化JSON数据为指定类型对象
    /// </summary>
    /// <typeparam name="T">目标类型</typeparam>
    /// <param name="json">JSON字符串</param>
    /// <returns>反序列化的对象</returns>
    public static T Deserialize<T>(string json)
    {
        try
        {
            return JsonConvert.DeserializeObject<T>(json);
        }
        catch (Exception e)
        {
            Debug.LogError($"反序列化数据失败: {e.Message}");
            return default(T);
        }
    }
}