
using ZGame;

public class SelectPlayerView : ViewBase
{
    public SelectPlayerView() : base("SelectPlayer")
    {
    }

    public override void OnInitialize()
    {
        LogUtils.Log("初始化选择角色界面");
    }

    public override void OnShow()
    {
        // 监听事件
        LogUtils.Log("加载选择角色界面");
    }

    public override void OnHide()
    {
        LogUtils.Log("卸载选择角色界面");
    }
}