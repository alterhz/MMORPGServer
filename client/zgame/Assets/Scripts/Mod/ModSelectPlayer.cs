using System;
using ZGame;

public class ModSelectPlayer : ModBase
{
    public override void Initialize()
    {
        LogUtils.Log("ModSelectPlayer Initialized");
    }

    protected override void OnDisable()
    {
        LogUtils.Log("ModSelectPlayer Disabled");
        
    }

    protected override void OnEnable()
    {
       LogUtils.Log("ModSelectPlayer Enabled");
    }
}