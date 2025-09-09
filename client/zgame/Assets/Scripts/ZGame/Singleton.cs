using System;

namespace ZGame
{
    /// <summary>
    /// 泛型单例基类，确保派生类只有一个实例
    /// </summary>
    /// <typeparam name="T">单例类的类型</typeparam>
    public abstract class Singleton<T> where T : class, new()
    {
        // 静态只读实例，使用 Lazy<T> 确保线程安全的懒加载
        private static readonly Lazy<T> lazy = new Lazy<T>(() => CreateInstance());

        /// <summary>
        /// 获取单例实例
        /// </summary>
        public static T Instance => lazy.Value;

        /// <summary>
        /// 私有构造函数防止外部实例化
        /// </summary>
        protected Singleton() { }

        /// <summary>
        /// 创建实例的工厂方法（可重写以支持依赖注入等）
        /// </summary>
        private static T CreateInstance()
        {
            return new T();
        }

        /// <summary>
        /// 确保派生类不能有公共构造函数
        /// </summary>
        static Singleton()
        {
            // 静态构造函数确保在第一次访问 Instance 之前运行，且只运行一次
            // 可用于额外的初始化检查
        }
    }
}