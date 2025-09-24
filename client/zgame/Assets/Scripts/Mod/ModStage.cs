using System;
using ZGame;

public class ModStage : ModBase
{
    public override void Initialize()
    {
        LogUtils.Log("ModStage Initialized");
    }

    protected override void OnEnable()
    {
        LogUtils.Log("ModStage Enabled");
    }

    protected override void OnDisable()
    {
        LogUtils.Log("ModStage Disabled");
    }

    /// <summary>
    /// 收到场景准备就绪消息处理
    /// </summary>
    [ProtoListener]
    private void OnStageReady(ScStageReady scStageReady)
    {
        LogUtils.Log($"收到场景准备就绪消息，场景SN: {scStageReady.stageSn}");
        
        // 发送进入场景请求
        CsEnterStage request = new();
        Send(request);
        LogUtils.Log("已发送进入场景请求");
    }

    /// <summary>
    /// 收到进入场景响应处理
    /// </summary>
    [ProtoListener]
    private void OnEnterStage(ScEnterStage scEnterStage)
    {
        LogUtils.Log($"收到进入场景响应，场景SN: {scEnterStage.stageSn}, X坐标: {scEnterStage.x}, Y坐标: {scEnterStage.y}");
    }
}