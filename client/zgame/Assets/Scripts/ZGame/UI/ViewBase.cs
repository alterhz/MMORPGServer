using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace ZGame
{
    /// <summary>
    /// View基类，继承自UIBase
    /// </summary>
    public abstract class ViewBase : UIBase
    {
        protected ViewBase(string canvasPath, string canvasName = null) : base(canvasPath, canvasName)
        {
        }

        public abstract void OnShow();
        public abstract void OnHide();

// 注册Canvas
        protected void RegisterCanvas(bool setAsActive = false)
        {
            UIManager.Instance.RegisterCanvas(this, setAsActive);
        }

        protected void ShowCanvas(string canvasName)
        {
            UIManager.Instance.ShowCanvas(canvasName);
        }

        protected void HideCanvas(string canvasName)
        {
            UIManager.Instance.HideCanvas(canvasName);
        }

        // 显示Canvas
        protected void ShowCanvas()
        {
            UIManager.Instance.ShowCanvas(CanvasName);
        }

        // 隐藏Canvas
        protected void HideCanvas()
        {
            UIManager.Instance.HideCanvas(CanvasName);
        }


        public T GetMod<T>() where T : ModBase
        {
            return ModManager.Instance.GetMod<T>();
        }

        
    }
}