package org.game.stage.module;

import org.game.stage.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * AOI九宫格中的单个格子
 */
public class Grid {
    // 格子坐标
    private final int x;
    private final int y;
    
    // 格子大小（长=宽）
    private final int size;
    
    // 格子中的实体列表
    private final List<Entity> entities = new ArrayList<>();
    
    public Grid(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
    
    /**
     * 添加实体到格子中
     * @param entity 实体
     */
    public void addEntity(Entity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
        }
    }
    
    /**
     * 从格子中移除实体
     * @param entity 实体
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }
    
    /**
     * 获取格子中的所有实体
     * @return 实体列表
     */
    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grid grid = (Grid) obj;
        return x == grid.x && y == grid.y;
    }
    
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}