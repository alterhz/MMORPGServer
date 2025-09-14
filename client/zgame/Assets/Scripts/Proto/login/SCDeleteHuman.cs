namespace ZGame
{
    [Proto(1010)]
    public partial class ScDeleteHuman
    {
        public long code { get; set; }
        public string humanId { get; set; }
        public string message { get; set; }
    }
}