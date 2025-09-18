package org.game.proto.scene;

public class Unit {
    private long unitId;
    private String name;
    private Position position;
    private long currentHealth;
    private long maxHealth;

    public long getUnitId() { return unitId; }
    public void setUnitId(long value) { this.unitId = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public Position getPosition() { return position; }
    public void setPosition(Position value) { this.position = value; }

    public long getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(long value) { this.currentHealth = value; }

    public long getMaxHealth() { return maxHealth; }
    public void setMaxHealth(long value) { this.maxHealth = value; }
}
