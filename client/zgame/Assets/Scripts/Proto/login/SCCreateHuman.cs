namespace ZGame
{
    [Proto(1008)]
    public partial class ScCreateHuman
    {
        public long code { get; set; }
        public string humanId { get; set; }
        public string message { get; set; }
        public bool success { get; set; }
    }
}