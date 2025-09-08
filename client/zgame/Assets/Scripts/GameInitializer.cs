using UnityEngine;
using ZGame.Mod;

public class GameInitializer : MonoBehaviour
{
    private NettyClient client;

    void Start()
    {
        UIManager.Instance.InitializeUIRoot();

        // 初始化网络
        ClientManager.Instance.InitClient();

        // 模型注册
        ModManager.Instance.RegisterMod(new ModLogin());

        // 创建并初始化登录管理器
        new LoginView();

        // 创建并初始化主界面管理器
        new MainView();

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