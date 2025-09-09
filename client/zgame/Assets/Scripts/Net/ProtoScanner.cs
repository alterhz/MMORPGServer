using System;
using System.Collections.Generic;
using System.Reflection;
using UnityEngine;

/// <summary>
/// 协议扫描器，用于扫描项目中的协议类并建立协议ID与类的映射关系
/// </summary>
public class ProtoScanner
{
    /// <summary>
    /// 协议类与协议ID的映射关系
    /// </summary>
    public static readonly Dictionary<Type, int> PROTO_ID_MAP = new Dictionary<Type, int>();
    
    /// <summary>
    /// 协议ID与协议类的反向映射关系
    /// </summary>
    public static readonly Dictionary<int, Type> ID_PROTO_MAP = new Dictionary<int, Type>();
    
    /// <summary>
    /// 根据协议类获取协议ID
    /// </summary>
    /// <param name="clazz">协议类类型</param>
    /// <returns>协议ID</returns>
    public static int GetProtoID(Type clazz)
    {
        if (PROTO_ID_MAP.ContainsKey(clazz))
        {
            return PROTO_ID_MAP[clazz];
        }
        return -1; // 未找到对应的协议ID
    }
    
    /// <summary>
    /// 根据协议ID获取协议类
    /// </summary>
    /// <param name="protoId">协议ID</param>
    /// <returns>协议类类型</returns>
    public static Type GetProtoClass(int protoId)
    {
        if (ID_PROTO_MAP.ContainsKey(protoId))
        {
            return ID_PROTO_MAP[protoId];
        }
        return null; // 未找到对应的协议类
    }
    
    /// <summary>
    /// 扫描当前程序集中包含Proto特性的类，并建立协议ID与类的映射关系
    /// </summary>
    public static void Initialize()
    {
        // 获取当前程序集中的所有类型
        Assembly assembly = Assembly.GetExecutingAssembly();
        Type[] types = assembly.GetTypes();
        
        foreach (Type type in types)
        {
            // 检查类是否包含Proto特性
            Proto protoAttribute = type.GetCustomAttribute<Proto>();
            if (protoAttribute != null)
            {
                int protoId = protoAttribute.ProtocolId;
                PROTO_ID_MAP[type] = protoId;
                ID_PROTO_MAP[protoId] = type;
                Debug.Log($"通过特性建立协议映射: {type.Name} <-> {protoId}");
            }
        }
        
        Debug.Log($"通过特性扫描完成，共找到 {PROTO_ID_MAP.Count} 个协议类");
    }
}