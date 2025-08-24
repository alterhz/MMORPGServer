using UnityEngine;

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

        // 创建并初始化登录管理器
        LoginManager loginManager = new LoginManager();

        // 创建并初始化主界面管理器
        MainManager mainManager = new MainManager();

        // 初始化网络
        ClientManager.Instance.InitClient();
    }

    void Update()
    {
        ClientManager.Instance.Run();
    }
}