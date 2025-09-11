# unity核心开发思路：
- Mod是逻辑代码：通过注解ProtoListener进行消息监听
- View是界面UI：EventListener实现事件监听
- UI通过Event实现与Mod解耦：View可以获取Mod并调用Mod的逻辑，但Mod不能持有View任何引用，只能通过Event事件投递，View监听Event处理。