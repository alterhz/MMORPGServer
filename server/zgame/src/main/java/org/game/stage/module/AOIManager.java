package org.game.stage.module;

import org.game.stage.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AOI九宫格管理器
 */
public class AOIManager {
    // 场景宽度
    private final int width;
    // 场景高度
    private final int height;
    // 格子大小
    private final int gridSize;
    // 九宫格数组
    private final Grid[][] grids;
    // X轴格子数量
    private final int gridWidth;
    // Y轴格子数量
    private final int gridHeight;

    public AOIManager(int width, int height, int gridSize) {
        this.width = width;
        this.height = height;
        this.gridSize = gridSize;
        
        // 计算九宫格数量
        this.gridWidth = (width + gridSize - 1) / gridSize;
        this.gridHeight = (height + gridSize - 1) / gridSize;
        
        // 初始化九宫格数组
        this.grids = new Grid[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grids[x][y] = new Grid(x, y, gridSize);
            }
        }
    }
    
    /**
     * 根据坐标获取格子
     * @param x X坐标
     * @param y Y坐标
     * @return 对应的格子
     */
    public Grid getGrid(int x, int y) {
        int gridX = x / gridSize;
        int gridY = y / gridSize;
        
        // 检查边界
        if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) {
            return null;
        }
        
        return grids[gridX][gridY];
    }
    
    /**
     * 获取指定格子及其周围的8个格子（共9个格子）
     * @param centerX 中心格子X坐标
     * @param centerY 中心格子Y坐标
     * @return 附近的9个格子列表
     */
    public List<Grid> getNeighbors(int centerX, int centerY) {
        List<Grid> neighbors = new ArrayList<>();
        
        // 遍历周围的9个格子（包括自己）
        for (int x = Math.max(0, centerX - 1); x <= Math.min(gridWidth - 1, centerX + 1); x++) {
            for (int y = Math.max(0, centerY - 1); y <= Math.min(gridHeight - 1, centerY + 1); y++) {
                neighbors.add(grids[x][y]);
            }
        }
        
        return neighbors;
    }
    
    /**
     * 获取两个九宫格列表的交集
     * @param grids1 第一个格子列表
     * @param grids2 第二个格子列表
     * @return 交集
     */
    public List<Grid> intersection(List<Grid> grids1, List<Grid> grids2) {
        Set<Grid> set1 = new HashSet<>(grids1);
        Set<Grid> set2 = new HashSet<>(grids2);
        set1.retainAll(set2);
        return new ArrayList<>(set1);
    }
    
    /**
     * 获取两个九宫格列表的并集
     * @param grids1 第一个格子列表
     * @param grids2 第二个格子列表
     * @return 并集
     */
    public List<Grid> union(List<Grid> grids1, List<Grid> grids2) {
        Set<Grid> set = new HashSet<>(grids1);
        set.addAll(grids2);
        return new ArrayList<>(set);
    }
    
    /**
     * 获取两个九宫格列表的差集 (grids1 - grids2)
     * @param grids1 第一个格子列表
     * @param grids2 第二个格子列表
     * @return 差集
     */
    public List<Grid> difference(List<Grid> grids1, List<Grid> grids2) {
        Set<Grid> set = new HashSet<>(grids1);
        set.removeAll(grids2);
        return new ArrayList<>(set);
    }
    
    /**
     * 获取所有格子中的实体
     * @return 所有实体列表
     */
    public List<Entity> getAllEntities() {
        List<Entity> allEntities = new ArrayList<>();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                allEntities.addAll(grids[x][y].getEntities());
            }
        }
        return allEntities;
    }
    
    /**
     * 获取指定格子列表中的所有实体
     * @param grids 格子列表
     * @return 实体列表
     */
    public List<Entity> getEntitiesInGrids(List<Grid> grids) {
        List<Entity> entities = new ArrayList<>();
        for (Grid grid : grids) {
            entities.addAll(grid.getEntities());
        }
        return entities;
    }
    
    public int getGridWidth() {
        return gridWidth;
    }
    
    public int getGridHeight() {
        return gridHeight;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getGridSize() {
        return gridSize;
    }
}