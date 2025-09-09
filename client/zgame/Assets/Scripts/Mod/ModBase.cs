using System;

namespace ZGame
{
    /// <summary>
    /// 所有Mod类的基类，提供Mod的基本生命周期和通用功能
    /// </summary>
    public abstract class ModBase
    {
        /// <summary>
        /// Mod的唯一标识符
        /// </summary>
        public string ModId { get; private set; }

        /// <summary>
        /// Mod是否已启用
        /// </summary>
        public bool IsEnabled { get; private set; }

        /// <summary>
        /// Mod是否已初始化
        /// </summary>
        public abstract void Initialize();

        /// <summary>
        /// 启用Mod
        /// </summary>
        public virtual void Enable()
        {
            IsEnabled = true;
            OnEnable();
        }

        /// <summary>
        /// 禁用Mod
        /// </summary>
        public virtual void Disable()
        {
            IsEnabled = false;
            OnDisable();
        }

        /// <summary>
        /// Mod更新方法
        /// </summary>
        public virtual void OnUpdate()
        {
            // 子类可以重写此方法以实现特定逻辑
        }

        /// <summary>
        /// Mod销毁时调用的方法
        /// </summary>
        public virtual void OnDestroyMod()
        {
            if (IsEnabled)
            {
                Disable();
            }
            OnDestroy();
        }

        /// <summary>
        /// 当Mod被启用时调用
        /// </summary>
        protected abstract void OnEnable();

        /// <summary>
        /// 当Mod被禁用时调用
        /// </summary>
        protected abstract void OnDisable();

        /// <summary>
        /// 当Mod被销毁时调用
        /// </summary>
        protected virtual void OnDestroy()
        {
            // 子类可以重写此方法以实现特定逻辑
        }
    }
}