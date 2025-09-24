namespace ZGame
{
    [Proto(1008)]
    public partial class ScCreatePlayer
    {
        public long code { get; set; }
        public long playerId { get; set; }
        public string message { get; set; }
        public bool success { get; set; }
    }
}