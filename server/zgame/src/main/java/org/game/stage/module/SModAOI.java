package org.game.stage.module;

import org.game.stage.StageObject;

import java.util.List;

public class SModAOI extends StageModBase {
    // AOI管理器
    private final AOIManager aoiManager;
    
    // 假设场景大小为1000x1000，格子大小为100
    private static final int SCENE_WIDTH = 1000;
    private static final int SCENE_HEIGHT = 1000;
    private static final int GRID_SIZE = 100;

    public SModAOI(StageObject stageObj) {
        super(stageObj);
        this.aoiManager = new AOIManager(SCENE_WIDTH, SCENE_HEIGHT, GRID_SIZE);
    }
    
    /**
     * 获取AOI管理器
     * @return AOI管理器
     */
    public AOIManager getAoiManager() {
        return aoiManager;
    }
    
    /**
     * 根据坐标获取格子
     * @param x X坐标
     * @param y Y坐标
     * @return 对应的格子
     */
    public Grid getGrid(int x, int y) {
        return aoiManager.getGrid(x, y);
    }
    
    /**
     * 获取指定格子的周围9个格子
     * @param centerX 中心格子X坐标
     * @param centerY 中心格子Y坐标
     * @return 周围的格子列表
     */
    public List<Grid> getNeighbors(int centerX, int centerY) {
        return aoiManager.getNeighbors(centerX, centerY);
    }
}