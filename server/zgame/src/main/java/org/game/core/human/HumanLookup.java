package org.game.core.human;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.game.human.HumanObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人物查找
 */
public class HumanLookup {

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

    public static HumanLocation getByHumanIdSafely(String humanId) {
        return humanIdMap.get(humanId);
    }

    public static HumanLocation getByAccountSafely(String account) {
        return accountMap.get(account);
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
