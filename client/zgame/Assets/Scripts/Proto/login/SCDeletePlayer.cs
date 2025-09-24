namespace ZGame
{
    [Proto(1010)]
    public partial class ScDeletePlayer
    {
        public long code { get; set; }
        public long playerId { get; set; }
        public string message { get; set; }
    }
}