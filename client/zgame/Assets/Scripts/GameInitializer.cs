using UnityEngine;
using ZGame.Mod;

public class GameInitializer : MonoBehaviour
{
    private NettyClient client;

    void Start()
    {
        // 确保UIManager已初始化
        if (UIManager.Instance == null)
        {
            Debug.LogError("UIManager未初始化");
            return;
        }

        // 初始化网络
        ClientManager.Instance.InitClient();

        ModManager.Instance.RegisterMod(new ModLogin());  

        // 创建并初始化登录管理器
        new LoginView();

        // 创建并初始化主界面管理器
        new MainView();

    }

    void Update()
    {
        ClientManager.Instance.Run();
    }
}