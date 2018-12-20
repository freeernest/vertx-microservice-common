package com.bigpanda.commons.streams;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.streams.WriteStream;

/**
 * Created by erik on 7/16/17.
 */
public interface BackPressuredWriteStream<T> extends WriteStream<T> {

    static <T> BackPressuredWriteStream create(Handler<T> writeHandler) {
        return new BackPressuredWriteStreamImpl<T>(writeHandler);
    }

    static <T> BackPressuredWriteStream createThrottled(Handler<T> writeHandler, long quotaPeriod, int quota, String persistentQuotaTimeFile, Vertx vertx) {
        return new ThrottleStreamImpl(writeHandler, quotaPeriod, quota, persistentQuotaTimeFile, vertx);
    }

    void drop();

    long getQueueSize();
}
