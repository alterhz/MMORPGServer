package org.game.stage.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.event.IEvent;
import org.game.core.event.UnitEventDispatcher;
import org.game.core.stage.UModScanner;
import org.game.core.utils.Vector3;
import org.game.stage.StageObject;
import org.game.stage.human.HumanObject;
import org.game.stage.module.Grid;
import org.game.stage.module.SModAOI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UnitObject extends Entity {
    private static final Logger logger = LogManager.getLogger(UnitObject.class);
    
    // 当前所在的格子
    private Grid currentGrid;
    
    private final Map<Class<?>, UnitModBase> unitModBaseMap = new HashMap<>();

    public UnitObject(long entityId, StageObject stageObj) {
        super(entityId, stageObj);
        initMods();
    }

    /**
     * 初始化模块
     */
    private void initMods() {
        List<Class<? extends UnitModBase>> unitModClasses = UModScanner.getStageUnitModClasses();
        for (Class<? extends UnitModBase> modClass : unitModClasses) {
            try {
                // 使用带参数的构造函数创建实例
                UnitModBase uModBase = modClass.getConstructor(UnitObject.class).newInstance(this);
                unitModBaseMap.put(modClass, uModBase);
            } catch (Exception e) {
                logger.error("StageUnitModBase init error", e);
            }
        }
        logger.info("{} 加载完成 {} 个StageUnitModBase", this, unitModClasses.size());
    }

    /**
     * 获取模块
     * @param clazz 模块类
     * @param <T> 模块类型
     * @return 模块实例
     */
    public <T extends UnitModBase> T getUMod(Class<T> clazz) {
        return (T) unitModBaseMap.get(clazz);
    }

    /**
     * 获取模块基础类
     * @param clazz 模块类
     * @return 模块基础实例
     */
    public UnitModBase getUModBase(Class<?> clazz) {
        return unitModBaseMap.get(clazz);
    }

    /**
     * 触发事件
     * @param event 事件对象
     */
    public void fireEvent(IEvent event) {
        String eventKey = event.getClass().getSimpleName().toLowerCase();
        UnitEventDispatcher.getInstance().dispatch(eventKey, (method) -> {
            // 根据method找到对应的mod实例
            Class<?> modClass = method.getDeclaringClass();
            return unitModBaseMap.get(modClass);
        }, event);
    }

    /**
     * 进入格子
     * @param grid 格子
     */
    protected void enterGrid(Grid grid) {
        if (this.currentGrid != null) {
            // 如果已经在某个格子中，先离开旧格子
            leaveGrid(this.currentGrid);
        }
        
        this.currentGrid = grid;
        if (grid != null) {
            grid.addEntity(this);
            
            // 通知周围的玩家有新单位出现
            notifyUnitAppear();
        }
    }

    /**
     * 离开格子
     * @param grid 格子
     */
    protected void leaveGrid(Grid grid) {
        if (this.currentGrid == grid && grid != null) {
            grid.removeEntity(this);
            this.currentGrid = null;
            
            // 通知周围的玩家有单位消失
            notifyUnitDisappear();
        }
    }

    /**
     * 改变格子
     * @param oldGrid 旧格子
     * @param newGrid 新格子
     */
    public void changeGrid(Grid oldGrid, Grid newGrid) {
        if (oldGrid != null) {
            oldGrid.removeEntity(this);
        }
        
        this.currentGrid = newGrid;
        if (newGrid != null) {
            newGrid.addEntity(this);
        }
        
        // 通知相关玩家单位出现或消失
        handleGridChange(oldGrid, newGrid);
    }

    /**
     * 处理格子变化
     * @param oldGrid 旧格子
     * @param newGrid 新格子
     */
    private void handleGridChange(Grid oldGrid, Grid newGrid) {
        SModAOI aoiMod = stageObj.getMod(SModAOI.class);
        if (aoiMod == null) return;

        List<Grid> oldNeighbors = oldGrid != null ? 
            aoiMod.getNeighbors(oldGrid.getX(), oldGrid.getY()) : new ArrayList<>();
        List<Grid> newNeighbors = newGrid != null ? 
            aoiMod.getNeighbors(newGrid.getX(), newGrid.getY()) : new ArrayList<>();
        
        // 找出新增的可见区域（在新邻居中但不在旧邻居中的格子）
        List<Grid> addedGrids = aoiMod.getAoiManager().difference(newNeighbors, oldNeighbors);
        // 找出消失的可见区域（在旧邻居中但不在新邻居中的格子）
        List<Grid> removedGrids = aoiMod.getAoiManager().difference(oldNeighbors, newNeighbors);
        
        // 通知新增区域中的玩家有新单位出现
        for (Grid grid : addedGrids) {
            for (Entity entity : grid.getEntities()) {
                if (entity instanceof HumanObject) {
                    HumanObject human = (HumanObject) entity;
                    // 发送本单位在新区域出现的广播给其他玩家
                    human.sendUnitAppear(this);
                }
            }
        }
        
        // 通知消失区域中的玩家有单位消失
        for (Grid grid : removedGrids) {
            for (Entity entity : grid.getEntities()) {
                if (entity instanceof HumanObject) {
                    HumanObject human = (HumanObject) entity;
                    // 发送本单位在旧区域消失的广播给其他玩家
                    human.sendUnitDisappear(this);
                }
            }
        }
    }

    /**
     * 通知单位出现
     */
    private void notifyUnitAppear() {
        if (currentGrid == null) return;
        
        SModAOI aoiMod = stageObj.getMod(SModAOI.class);
        if (aoiMod == null) return;
        
        List<Grid> neighbors = aoiMod.getNeighbors(currentGrid.getX(), currentGrid.getY());
        List<Entity> entities = aoiMod.getAoiManager().getEntitiesInGrids(neighbors);
        
        for (Entity entity : entities) {
            if (entity instanceof HumanObject && entity != this) {
                HumanObject human = (HumanObject) entity;
                // 发送本单位出现的广播给其他玩家
                human.sendUnitAppear(this);
            }
        }
    }

    /**
     * 通知单位消失
     */
    private void notifyUnitDisappear() {
        if (currentGrid == null) return;
        
        SModAOI aoiMod = stageObj.getMod(SModAOI.class);
        if (aoiMod == null) return;
        
        List<Grid> neighbors = aoiMod.getNeighbors(currentGrid.getX(), currentGrid.getY());
        List<Entity> entities = aoiMod.getAoiManager().getEntitiesInGrids(neighbors);
        
        for (Entity entity : entities) {
            if (entity instanceof HumanObject && entity != this) {
                HumanObject human = (HumanObject) entity;
                // 发送本单位消失的广播给其他玩家
                human.sendUnitDisappear(this);
            }
        }
    }

    /**
     * 获取当前格子
     * @return 当前格子
     */
    public Grid getCurrentGrid() {
        return currentGrid;
    }

    /**
     * 设置位置并检查九宫格切换
     * @param position 新位置
     */
    @Override
    public void setPosition(Vector3 position) {
        Vector3 oldPosition = this.position;
        this.position = position;

        // 如果旧位置不为空，则检查是否需要切换格子
        if (oldPosition != null) {
            checkGridChange(oldPosition, position);
        }
    }

    /**
     * 检查并处理格子切换
     * @param oldPosition 旧位置
     * @param newPosition 新位置
     */
    private void checkGridChange(Vector3 oldPosition, Vector3 newPosition) {
        Grid currentGrid = getCurrentGrid();
        if (currentGrid == null) {
            return;
        }

        // 获取场景AOI模块
        var aoiMod = getStageObj().getMod(org.game.stage.module.SModAOI.class);
        if (aoiMod == null) {
            return;
        }

        // 计算新位置所在的格子
        Grid newGrid = aoiMod.getGrid((int)newPosition.getX(), (int)newPosition.getY());

        // 如果格子发生变化，调用changeGrid方法
        if (newGrid != currentGrid) {
            changeGrid(currentGrid, newGrid);
        }
    }

    @Override
    public void onEnterStage(StageObject stageObj) {
        // 当单位进入场景时，根据位置进入对应的格子
        if (position != null) {
            SModAOI aoiMod = stageObj.getMod(SModAOI.class);
            if (aoiMod != null) {
                Grid grid = aoiMod.getGrid((int)position.getX(), (int)position.getY());
                enterGrid(grid);
            }
        }
    }

    @Override
    public void onLeaveStage(StageObject stageObj) {
        // 当单位离开场景时，离开当前格子
        leaveGrid(currentGrid);
    }
    
    @Override
    public void onPulse(long now) {
        super.onPulse(now);
        
        unitModBaseMap.forEach((aClass, uModBase) -> uModBase.onPulse(now));
    }
    
    @Override
    public void onPulseSec(long now) {
        super.onPulseSec(now);
        
        unitModBaseMap.forEach((aClass, uModBase) -> uModBase.onPulseSec(now));
    }
}