using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using ZGame;

public class ConfirmPanel : PanelBase
{
    private readonly string _text;

    private Action _ok;
    private Action _cancel;

    public ConfirmPanel(string text, Action ok, Action cancel) : base("Confirm") // 显式调用基类构造函数
    {
        _text = text;
        _ok = ok;
        _cancel = cancel;
    }

    public static ConfirmPanel Create(string text, Action ok, Action cancel)
    {
        ConfirmPanel panel = new(text, ok, cancel);
        panel.OnInitialize();
        panel.Show();
        return panel;
    }

    public override void OnInitialize()
    {
         SetText("Text", _text);

        // OK和Cancel按钮的点击事件处理
        AddButtonClickListener("OK", () =>
        {
            LogUtils.Log("[ConfirmPanel] OK button clicked"); // 添加日志记录
            Hide();
            _ok?.Invoke();
        });

        AddButtonClickListener("Cancel", () =>
        {
            LogUtils.Log("[ConfirmPanel] Cancel button clicked"); // 添加日志记录
            Hide();
            _cancel?.Invoke();
        });
    }

}
