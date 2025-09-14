namespace ZGame
{
    using System.Collections.Generic;

    [Proto(1004)]
    public partial class ScQueryHuman
    {
        public long code { get; set; }
        public string message { get; set; }
        public List<Human> human { get; set; }
    }

    public partial class Human
    {
        public string id { get; set; }
        public string name { get; set; }
        public string profession { get; set; }
    }
}