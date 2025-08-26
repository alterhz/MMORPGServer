package org.game.core.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameThread;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class QuerySubscriber<T> implements Subscriber<T> {

    public static final Logger logger = LogManager.getLogger(QuerySubscriber.class);

    private final GameThread gameThread;
    private final Consumer<List<T>> consumer;
    private final long maxRequest;
    /**
     * DB数据
     */
    private final List<T> dbCollections = new ArrayList<>();

    public QuerySubscriber(Consumer<List<T>> consumer, long maxRequest) {
        this.gameThread = GameThread.getCurrentGameThread();
        this.consumer = consumer;
        this.maxRequest = maxRequest;
    }

    public QuerySubscriber(Consumer<List<T>> consumer) {
        this.gameThread = GameThread.getCurrentGameThread();
        this.consumer = consumer;
        this.maxRequest = 1L;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(maxRequest);
    }

    @Override
    public void onNext(T o) {
        dbCollections.add(o);
    }

    @Override
    public void onError(Throwable t) {
        logger.error("❌ 错误: {}", t.getMessage());
    }

    @Override
    public void onComplete() {
        gameThread.runTask(() -> {
            consumer.accept(dbCollections);
        });
    }
}
