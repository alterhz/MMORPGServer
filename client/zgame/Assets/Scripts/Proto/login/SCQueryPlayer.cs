namespace ZGame
{
    using System.Collections.Generic;

    [Proto(1004)]
    public partial class ScQueryPlayer
    {
        public long code { get; set; }
        public string message { get; set; }
        public List<Player> player { get; set; }
    }

    public partial class Player
    {
        public long id { get; set; }
        public string name { get; set; }
        public string profession { get; set; }
    }
}