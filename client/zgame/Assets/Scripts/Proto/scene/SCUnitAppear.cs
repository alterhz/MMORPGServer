namespace ZGame
{
    using System.Collections.Generic;

    [Proto(50003)]
    public partial class ScUnitAppear
    {
        public List<Unit> units { get; set; }
    }

    public partial class Unit
    {
        public long unitId { get; set; }
        public string name { get; set; }
        public Position position { get; set; }
        public long currentHealth { get; set; }
        public long maxHealth { get; set; }
    }

}