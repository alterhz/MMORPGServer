package org.game.core.player;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程安全的ID分配器（使用long）
 * 生成规则：serverId + sequence（拼接）
 * 例如：serverId=2000, sequence=1 -> ID=200000000001 (12位sequence)
 */
public class IdAllocator {

    private volatile int serverId; // serverId 用 int 足够
    private final AtomicLong sequence;

    // 顺序编码的位数（例如12位）
    private static final int SEQUENCE_DIGITS = 12;
    private static final long BASE = (long) Math.pow(10, SEQUENCE_DIGITS);

    public IdAllocator(int serverId, long startSequence) {
        this.serverId = serverId;
        this.sequence = new AtomicLong(Math.max(0, startSequence % BASE));
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setStartSequence(long startSequence) {
        sequence.set(Math.max(0, startSequence % BASE));
    }

    public long getCurrentSequence() {
        return sequence.get();
    }

    /**
     * 分配ID，返回 long
     */
    public long allocateId() {
        long currentSeq = sequence.getAndIncrement();
        return (long) serverId * BASE + currentSeq;
    }

    public void reset(int serverId, long startSequence) {
        this.serverId = serverId;
        sequence.set(Math.max(0, startSequence % BASE));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("serverId", serverId)
                .append("sequence", sequence)
                .toString();
    }
}
