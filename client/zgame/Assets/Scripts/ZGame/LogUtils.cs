using System;
using UnityEngine;

namespace ZGame
{
    /// <summary>
    /// 日志工具类，用于统一处理Unity中的日志输出
    /// </summary>
    public static class LogUtils
    {
        /// <summary>
        /// 日志标签前缀
        /// </summary>
        private const string LogTag = "[ZGame] ";

        /// <summary>
        /// 记录普通日志信息
        /// </summary>
        /// <param name="message">日志消息</param>
        /// <param name="context">上下文对象</param>
        public static void Log(object message, UnityEngine.Object context = null)
        {
            if (context == null)
                Debug.Log(LogTag + message);
            else
                Debug.Log(LogTag + message, context);
        }

        /// <summary>
        /// 记录警告日志信息
        /// </summary>
        /// <param name="message">日志消息</param>
        /// <param name="context">上下文对象</param>
        public static void LogWarning(object message, UnityEngine.Object context = null)
        {
            if (context == null)
                Debug.LogWarning(LogTag + message);
            else
                Debug.LogWarning(LogTag + message, context);
        }

        /// <summary>
        /// 记录错误日志信息
        /// </summary>
        /// <param name="message">日志消息</param>
        /// <param name="context">上下文对象</param>
        public static void LogError(object message, UnityEngine.Object context = null)
        {
            if (context == null)
                Debug.LogError(LogTag + message);
            else
                Debug.LogError(LogTag + message, context);
        }

        /// <summary>
        /// 记录异常日志信息
        /// </summary>
        /// <param name="exception">异常对象</param>
        /// <param name="context">上下文对象</param>
        public static void LogException(Exception exception, UnityEngine.Object context = null)
        {
            if (context == null)
                Debug.LogException(exception);
            else
                Debug.LogException(exception, context);
        }

        /// <summary>
        /// 记录格式化日志信息
        /// </summary>
        /// <param name="format">包含零个或多个格式项的字符串</param>
        /// <param name="args">要格式化到format字符串中的零个或多个对象</param>
        public static void LogFormat(string format, params object[] args)
        {
            Debug.LogFormat(LogTag + format, args);
        }

        /// <summary>
        /// 记录格式化警告信息
        /// </summary>
        /// <param name="format">包含零个或多个格式项的字符串</param>
        /// <param name="args">要格式化到format字符串中的零个或多个对象</param>
        public static void LogWarningFormat(string format, params object[] args)
        {
            Debug.LogWarningFormat(LogTag + format, args);
        }

        /// <summary>
        /// 记录格式化错误信息
        /// </summary>
        /// <param name="format">包含零个或多个格式项的字符串</param>
        /// <param name="args">要格式化到format字符串中的零个或多个对象</param>
        public static void LogErrorFormat(string format, params object[] args)
        {
            Debug.LogErrorFormat(LogTag + format, args);
        }
    }
}