package com.bigpanda.commons.streams;

import io.vertx.core.Handler;
import io.vertx.core.streams.WriteStream;

/**
 * Created by erik on 7/16/17.
 */
class BackPressuredWriteStreamImpl<T> implements BackPressuredWriteStream<T> {

    private int maxSize;
    private long queueSize;
    private Handler<T> writeHandler;
    protected Handler<Void> handler;

    BackPressuredWriteStreamImpl(Handler<T> writeHandler) {
        this.writeHandler = writeHandler;
    }

    @Override
    public WriteStream<T> exceptionHandler(Handler<Throwable> handler) {
        return this;
    }

    @Override
    public synchronized WriteStream<T> write(T data) {
        writeHandler.handle(data);
        queueSize++;
        return this;
    }

    @Override
    public void end() {

    }

    @Override
    public void end(T t) {

    }

    @Override
    public WriteStream<T> setWriteQueueMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return queueSize > maxSize;
    }

    @Override
    public WriteStream<T> drainHandler(Handler<Void> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public synchronized void drop() {
        queueSize--;
        if (handler != null && queueSize < maxSize / 2) {
            releaseStream();
        }
    }

    @Override
    public long getQueueSize() {
        return queueSize;
    }

    protected void releaseStream() {
        if (!writeQueueFull()) {
            handler.handle(null);
            handler = null;
        }
    }
}
