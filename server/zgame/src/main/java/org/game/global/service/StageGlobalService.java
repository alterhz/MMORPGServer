package org.game.global.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameServiceBase;
import org.game.core.Param;
import org.game.core.rpc.ReferenceFactory;
import org.game.core.rpc.ToPoint;
import org.game.core.stage.StageThread;
import org.game.core.utils.SnowflakeIdGenerator;
import org.game.global.rpc.IStageGlobalService;
import org.game.stage.service.StageService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StageGlobalService extends GameServiceBase implements IStageGlobalService {

    public static final Logger logger = LogManager.getLogger(StageGlobalService.class);
    
    // 存储场景信息 key: stageId, value: 场景信息
    private final Map<Long, StageInfo> stageInfos = new HashMap<>();
    
    // 最大场景人数
    private static final int MAX_STAGE_HUMAN_COUNT = 50;
    
    // 场景回收时间阈值(30秒)
    private static final long STAGE_RECYCLE_TIME_THRESHOLD = 30 * 1000L;
    
    // 分配类型 - 人数最少的线
    public static final int ALLOC_TYPE_MIN_HUMAN = 1;
    
    // 分配类型 - 人数最多的线
    public static final int ALLOC_TYPE_MAX_HUMAN = 2;

    // 分配线程索引
    private int allocThreadIndex = 0;

    public StageGlobalService(String name) {
        super(name);
    }

    @Override
    public void init() {
        logger.info("StageGlobalService 初始化");
    }

    @Override
    public void startup() {
        logger.info("StageGlobalService 启动");
    }

    @Override
    protected void onPulseSec(long now) {
        super.onPulseSec(now);

        recycleTest();
    }

    @Override
    public void destroy() {
        logger.info("StageGlobalService 销毁");
    }

    @Override
    public void hotfix(Param param) {
        logger.info("StageGlobalService 热修复: param={}", param);
    }
    
    @Override
    public CompletableFuture<Param> getStageInfo(int stageSn, int allocType) {
        logger.info("获取场景信息: stageSn={}, allocType={}", stageSn, allocType);
        
        // 查找符合条件的场景
        Long targetStageId = findAvailableStage(stageSn, allocType);
        
        if (targetStageId != null) {
            // 找到可用场景
            StageInfo stageInfo = stageInfos.get(targetStageId);
            if (stageInfo != null) {
                Param result = new Param();
                result.put("stageSn", stageInfo.stageSn);
                result.put("stageId", stageInfo.stageId);
                result.put("toPoint", stageInfo.toPoint);
                logger.info("返回已有场景信息: {}", result);
                return CompletableFuture.completedFuture(result);
            }
        }
        
        // 没有找到可用场景，需要创建新场景
        return createNewStage(stageSn, allocType);
    }
    
    /**
     * 查找可用场景
     * @param stageSn 场景配置SN
     * @param allocType 分配类型
     * @return 场景ID，如果找不到返回null
     */
    private Long findAvailableStage(int stageSn, int allocType) {
        Long targetStageId = null;
        int targetHumanCount = allocType == ALLOC_TYPE_MIN_HUMAN ? Integer.MAX_VALUE : -1;
        
        for (Map.Entry<Long, StageInfo> entry : stageInfos.entrySet()) {
            Long stageId = entry.getKey();
            StageInfo stageInfo = entry.getValue();
            
            // 检查是否为相同类型的场景
            if (stageInfo.stageSn == stageSn) {
                int humanCount = stageInfo.humanCount;
                
                // 检查场景是否已满
                if (humanCount < MAX_STAGE_HUMAN_COUNT) {
                    // 根据分配类型选择合适的场景
                    if (allocType == ALLOC_TYPE_MIN_HUMAN && humanCount < targetHumanCount) {
                        targetHumanCount = humanCount;
                        targetStageId = stageId;
                    } else if (allocType == ALLOC_TYPE_MAX_HUMAN && humanCount > targetHumanCount) {
                        targetHumanCount = humanCount;
                        targetStageId = stageId;
                    }
                }
            }
        }
        
        return targetStageId;
    }
    
    /**
     * 创建新场景
     * @param stageSn 场景配置SN
     * @param allocType 分配类型
     * @return 场景信息
     */
    private CompletableFuture<Param> createNewStage(int stageSn, int allocType) {
        // 按顺序分配一个StageThread
        int stageThreadCount = StageThread.getStageThreadCount();
        if (stageThreadCount <= 0) {
            logger.error("没有可用的StageThread");
            Param errorResult = new Param();
            errorResult.put("error", "没有可用的场景线程");
            return CompletableFuture.completedFuture(errorResult);
        }
        
        // 使用轮询方式分配线程
        long stageId = SnowflakeIdGenerator.getInstance().nextId();
        int threadIndex = (++allocThreadIndex) % StageThread.getStageThreadCount();

        logger.info("创建新场景: stageSn={}, stageId={}, threadIndex={}", stageSn, stageId, threadIndex);
        
        // 创建场景信息
        StageInfo stageInfo = new StageInfo();
        stageInfo.stageSn = stageSn;
        stageInfo.stageId = stageId;
        String gameProcessName = GameProcess.getGameProcessName();
        String stageThreadName = StageThread.getStageThreadName(threadIndex);
        stageInfo.toPoint = new ToPoint(gameProcessName, stageThreadName, StageService.NAME);
        
        stageInfos.put(stageId, stageInfo);

        // TODO 调用StageService创建场景
        StageService stageService = ReferenceFactory.getProxy(StageService.class, stageInfo.toPoint);
        stageService.createCommonStage(stageSn);
        
        // 构造返回结果
        Param result = new Param();
        result.put("stageSn", stageSn);
        result.put("stageId", stageId);
        result.put("toPoint", stageInfo.toPoint);
        
        logger.info("创建新场景成功: {}", result);
        return CompletableFuture.completedFuture(result);
    }
    
    @Override
    public void humanEnter(long stageId) {
        logger.info("玩家进入场景: stageId={}", stageId);
        StageInfo stageInfo = stageInfos.get(stageId);
        if (stageInfo != null) {
            stageInfo.humanCount++;
            stageInfo.lastActiveTime = System.currentTimeMillis();
            logger.info("场景人数增加，当前人数: {}", stageInfo.humanCount);
        } else {
            logger.error("humanEnter未找到场景: stageId={}", stageId);
        }
    }
    
    @Override
    public void humanLeave(long stageId) {
        logger.info("玩家离开场景: stageId={}", stageId);
        StageInfo stageInfo = stageInfos.get(stageId);
        if (stageInfo != null) {
            stageInfo.humanCount--;
            stageInfo.lastActiveTime = System.currentTimeMillis();
            logger.info("场景人数减少，当前人数: {}", stageInfo.humanCount);
            if (stageInfo.humanCount < 0) {
                // 场景人数不能为0
                logger.error("场景人数不能为负数.stageInfo = {}", stageInfo);
            }
        } else {
            logger.error("humanLeave未找到场景: stageId={}", stageId);
        }
    }
    
    @Override
    public void recycleTest() {
        logger.info("开始检查可回收场景");
        long now = System.currentTimeMillis();
        
        for (Map.Entry<Long, StageInfo> entry : stageInfos.entrySet()) {
            Long stageId = entry.getKey();
            StageInfo stageInfo = entry.getValue();
            
            // 检查场景是否人数为0且超过阈值时间没有活跃
            if (stageInfo.humanCount == 0 && 
                now - stageInfo.lastActiveTime > STAGE_RECYCLE_TIME_THRESHOLD) {
                
                // 可以回收场景
                stageInfos.remove(stageId);
                logger.info("回收空闲场景: stageId={}", stageId);
            }
        }
    }
    
    /**
     * 场景信息类
     */
    private static class StageInfo {
        private int stageSn;
        private long stageId;
        private ToPoint toPoint;
        private int humanCount = 0;
        private long lastActiveTime = System.currentTimeMillis();
    }
}