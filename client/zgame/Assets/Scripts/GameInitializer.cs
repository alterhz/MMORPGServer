using UnityEngine;
using ZGame;

public class GameInitializer : MonoBehaviour
{
    private NettyClient client;

    void Start()
    {
        LogUtils.Log("GameInitializer Start");
        // 初始化协议ID
        ProtoScanner.Initialize();

        // 初始化网络
        ClientManager.Instance.InitClient();

        // 注册Mod
        ModScanner.ScanAndRegisterMods();

        // 初始化UI系统
        UIManager.Instance.InitializeUIRoot();
        // 注册View
        ViewScanner.ScanAndRegisterViews();

        UIManager.Instance.ShowCanvas(ViewNames.LOGIN);

        LogUtils.Log("GameInitializer End");
    }

    void Update()
    {
        // 渲染相关逻辑

    }

    void FixedUpdate()
    {
        // 网络，Mod等逻辑
        ClientManager.Instance.Run();
        ModManager.Instance.UpdateAllMods();
    }
    
    void Destroy()
    {
        // 清理资源
        ClientManager.Instance?.Destroy();
        ModManager.Instance?.Destroy();
    }
}