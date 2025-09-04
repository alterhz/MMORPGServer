package org.game.core.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameProcess;
import org.game.core.GameThread;
import org.game.human.HumanObject;
import org.game.human.service.HumanService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人物查找
 */
public class HumanLookup {

    public static final Logger logger = LogManager.getLogger(HumanLookup.class);

    private static final Map<String, HumanLocation> humanIdMap = new ConcurrentHashMap<>();
    private static final Map<String, HumanLocation> accountMap = new ConcurrentHashMap<>();

    public static void add(String humanId, String account, String threadName) {
        // 添加ID映射
        humanIdMap.put(humanId, new HumanLocation(humanId, threadName, account));
        // 添加账号映射
        accountMap.put(account, new HumanLocation(humanId, threadName, account));
    }

    public static void remove(String humanId) {
        HumanLocation humanLocation = humanIdMap.get(humanId);
        if (humanLocation != null) {
            humanIdMap.remove(humanId);
            accountMap.remove(humanLocation.getAccount());
        }
    }

    /**
     * 获取玩家线程名称
     */
    public static String getHumanThreadName(String humanId) {
        HumanLocation humanLocation = humanIdMap.get(humanId);
        if (humanLocation != null) {
            return humanLocation.getThreadName();
        }
        return null;
    }

    public static HumanLocation getByHumanIdSafely(String humanId) {
        return humanIdMap.get(humanId);
    }

    public static HumanLocation getByAccountSafely(String account) {
        return accountMap.get(account);
    }

    public static void KickHumanByAccount(String account) {
        HumanLocation humanLocation = accountMap.get(account);
        if (humanLocation != null) {
            GameThread humanThread = GameProcess.getGameThread(humanLocation.threadName);
            if (humanThread != null) {
                humanThread.runTask(() -> {
                    HumanService humanService = (HumanService)humanThread.getGameService(humanLocation.humanId);
                    if (humanService != null) {
                        HumanObject humanObj = humanService.getHumanObj();
                        humanObj.disconnect();
                        humanThread.removeGameService(humanService);
                    }
                });
            }
        }
    }

    public static class HumanLocation {
        private final String humanId;
        private final String threadName;
        private final String account;
        
        public HumanLocation(String humanId, String threadName, String account) {
            this.humanId = humanId;
            this.threadName = threadName;
            this.account = account;
        }

        public String getHumanId() {
            return humanId;
        }

        public String getThreadName() {
            return threadName;
        }

        public String getAccount() {
            return account;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("humanId", humanId)
                    .append("threadName", threadName)
                    .append("account", account)
                    .toString();
        }
    }

}
