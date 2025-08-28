package org.game.core.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.game.core.GameThread;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class QuerySubscriber<T> implements Subscriber<T> {

    public static final Logger logger = LogManager.getLogger(QuerySubscriber.class);

    private final GameThread gameThread;
    private final long maxRequest;
    /**
     * DB数据
     */
    private final List<T> dbCollections = new ArrayList<>();

    public QuerySubscriber(long maxRequest) {
        this.gameThread = GameThread.getCurrentGameThread();
        this.maxRequest = maxRequest;
    }

    public QuerySubscriber() {
        this.gameThread = GameThread.getCurrentGameThread();
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
        if (gameThread == null) {
            onError(t.getMessage());
        } else {
            gameThread.runTask(() -> {
                onError(t.getMessage());
            });
        }
    }

    protected abstract void onLoadDB(List<T> dbCollections);

    protected abstract void onError(String errMessage);

    @Override
    public void onComplete() {
        if (gameThread == null) {
            onLoadDB(dbCollections);
        } else {
            gameThread.runTask(() -> {
                onLoadDB(dbCollections);
            });
        }
    }
}
