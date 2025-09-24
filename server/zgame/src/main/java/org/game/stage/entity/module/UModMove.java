package org.game.stage.entity.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.event.EventListener;
import org.game.core.utils.Vector3;
import org.game.stage.entity.UnitObject;
import org.game.stage.entity.UnitModBase;
import org.game.stage.module.Grid;
import org.game.stage.human.HumanObject;
import org.game.stage.entity.Entity;
import org.game.proto.scene.Position;
import org.game.proto.scene.SCMoveStop;
import org.game.stage.module.SModAOI;

import java.util.List;

/**
 * UnitObject的移动模块
 * 实现单位的移动功能，包括方向移动和坐标点移动
 */
public class UModMove extends UnitModBase {
    private static final Logger logger = LogManager.getLogger(UModMove.class);

    // 移动目标位置
    private Vector3 targetPosition;
    
    // 移动方向
    private Vector3 moveDirection;
    
    // 移动速度（单位/秒）
    private float moveSpeed = 5.0f;
    
    // 是否正在移动
    private boolean isMoving = false;

    public UModMove(UnitObject unitObject) {
        super(unitObject);
    }

    /**
     * 设置移动到目标点
     * @param target 目标位置
     */
    public void moveTo(Vector3 target) {
        this.targetPosition = new Vector3(target);
        this.moveDirection = Vector3.subtract(targetPosition, unitObj.getPosition());
        this.moveDirection.normalize();
        this.isMoving = true;
        
        logger.debug("Unit {} move to target: {}", unitObj.getEntityId(), targetPosition);
    }

    /**
     * 设置移动方向
     * @param direction 移动方向
     */
    public void moveDirection(Vector3 direction) {
        this.moveDirection = new Vector3(direction);
        this.moveDirection.normalize();
        this.targetPosition = null;
        this.isMoving = true;
        
        logger.debug("Unit {} move in direction: {}", unitObj.getEntityId(), direction);
    }

    /**
     * 停止移动
     */
    public void stopMove() {
        this.isMoving = false;
        this.targetPosition = null;
        this.moveDirection = null;

        logger.debug("Unit {} stop moving", unitObj.getEntityId());

        // 广播移动停止
        SCMoveStop moveStop = new SCMoveStop();
        moveStop.setUnitId(unitObj.getEntityId());
        
        Position pos = new Position();
        Vector3 position = unitObj.getPosition();
        if (position != null) {
            pos.setX(position.getX());
            pos.setY(position.getY());
            pos.setZ(position.getZ());
        }
        moveStop.setPosition(pos);
        
        // 获取场景中的AOI模块
        var aoiMod = unitObj.getStageObj().getMod(org.game.stage.module.SModAOI.class);
        if (aoiMod != null && position != null) {
            // 获取周围的格子
            List<Grid> neighbors = aoiMod.getNeighbors(
                (int) position.getX(), 
                (int) position.getY()
            );
            
            // 向周围的所有玩家广播停止移动消息
            for (Grid grid : neighbors) {
                for (Entity entity : grid.getEntities()) {
                    if (entity instanceof HumanObject) {
                        HumanObject nearbyHuman = (HumanObject) entity;
                        nearbyHuman.sendMessage(moveStop);
                    }
                }
            }
        }
    }

    /**
     * 设置移动速度
     * @param speed 移动速度
     */
    public void setMoveSpeed(float speed) {
        this.moveSpeed = speed;
    }

    /**
     * 获取移动速度
     * @return 移动速度
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * 是否正在移动
     * @return 是否正在移动
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * 心跳处理移动逻辑
     * @param now 当前时间戳
     */
    @Override
    public void onPulse(long now) {
        super.onPulse(now);
        
        // 如果没有移动则直接返回
        if (!isMoving) {
            return;
        }
        
        // 计算这一帧的移动距离
        float deltaTime = 0.05f; // 假设50ms一帧
        float distance = moveSpeed * deltaTime;
        
        Vector3 currentPosition = unitObj.getPosition();
        Vector3 newPosition;
        
        // 根据移动模式计算新位置
        if (targetPosition != null) {
            // 向目标点移动
            Vector3 direction = Vector3.subtract(targetPosition, currentPosition);
            float distanceToTarget = direction.magnitude();
            
            if (distanceToTarget <= distance) {
                // 到达目标点
                newPosition = new Vector3(targetPosition);
                stopMove();
            } else {
                // 还未到达目标点
                direction.normalize();
                newPosition = Vector3.add(currentPosition, Vector3.multiply(direction, distance));
            }
        } else if (moveDirection != null) {
            // 向指定方向移动
            newPosition = Vector3.add(currentPosition, Vector3.multiply(moveDirection, distance));
        } else {
            return;
        }
        
        // 更新位置
        unitObj.setPosition(newPosition);
        
        // 如果是玩家对象，广播移动消息
        if (unitObj instanceof HumanObject) {
            HumanObject humanObj = (HumanObject) unitObj;
            // 获取场景中的AOI模块
            var aoiMod = unitObj.getStageObj().getMod(SModAOI.class);
            if (aoiMod != null) {
                List<Entity> entities = aoiMod.getAoiManager().getEntitiesInNeighbors((int)newPosition.getX(), (int)newPosition.getY());
                for (Entity entity : entities) {
                    if (entity instanceof HumanObject) {
                        HumanObject nearbyHuman = (HumanObject) entity;
                        nearbyHuman.sendUnitMove(unitObj.getEntityId(), newPosition);
                    }
                }
            }
        }
    }
}