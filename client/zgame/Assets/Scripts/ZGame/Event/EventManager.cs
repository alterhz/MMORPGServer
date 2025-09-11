using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using ZGame;
using System.Reflection;

public class EventManager : Singleton<EventManager>
{
    private readonly MethodDispatcher _methodDispatcher = new();

    public void Register(string eventType, object obj, MethodInfo methodInfo)
    {
        _methodDispatcher.RegisterMethod(eventType, obj, methodInfo);
    }

    public void Unregister(string eventType, object obj, MethodInfo methodInfo)
    {
        _methodDispatcher.UnregisterMethod(eventType, obj, methodInfo);
    }

    public void Trigger(object param)
    {
        string eventType = param.GetType().Name;
        _methodDispatcher.InvokeMethod(eventType, param);
    }

    public void Clear(string eventType)
    {
        _methodDispatcher.ClearEvent(eventType);
    }

    public void ClearAll()
    {
        _methodDispatcher.ClearAll();
    }


}
