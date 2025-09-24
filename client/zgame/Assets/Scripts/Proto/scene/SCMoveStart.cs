namespace ZGame
{
    using System.Collections.Generic;

    [Proto(50006)]
    public partial class ScMoveStart
    {
        public long unitId { get; set; }
        public List<Position> position { get; set; }
    }

    public partial class Position
    {
        public double x { get; set; }
        public double y { get; set; }
        public double z { get; set; }
    }
}