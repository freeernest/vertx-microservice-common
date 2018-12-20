package com.bigpanda.commons.security.context;

import com.bigpanda.commons.services.proxy.ServiceContext;
import com.bigpanda.commons.services.proxy.ServiceContextHolder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.*;

class ContextualEventBus implements EventBus {
    private EventBus eventBus;

    public ContextualEventBus(Vertx vertx) {
        this.eventBus = vertx.eventBus();
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public EventBus send(String address, Object message) {
        return eventBus.send(address, message);
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public <T> EventBus send(String address, Object message, Handler<AsyncResult<Message<T>>> replyHandler) {
        return eventBus.send(address, message, replyHandler);
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public EventBus send(String address, Object message, DeliveryOptions options) {
        return eventBus.send(address, message, options);
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public <T> EventBus send(String address, Object message, DeliveryOptions options, Handler<AsyncResult<Message<T>>> replyHandler) {
        ServiceContext context = ServiceContextHolder.getContext();
        if (context != null) {
            options.addHeader("context", context.toJson().encode());
        }
        return eventBus.send(address, message, options, replyHandler);
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public EventBus publish(String address, Object message) {
        return eventBus.publish(address, message);
    }

    @Override
    @io.vertx.codegen.annotations.Fluent
    public EventBus publish(String address, Object message, DeliveryOptions options) {
        return eventBus.publish(address, message, options);
    }

    @Override
    public <T> MessageConsumer<T> consumer(String address) {
        return eventBus.consumer(address);
    }

    @Override
    public <T> MessageConsumer<T> consumer(String address, Handler<Message<T>> handler) {
        return eventBus.consumer(address, handler);
    }

    @Override
    public <T> MessageConsumer<T> localConsumer(String address) {
        return eventBus.localConsumer(address);
    }

    @Override
    public <T> MessageConsumer<T> localConsumer(String address, Handler<Message<T>> handler) {
        return eventBus.localConsumer(address, handler);
    }

    @Override
    public <T> MessageProducer<T> sender(String address) {
        return eventBus.sender(address);
    }

    @Override
    public <T> MessageProducer<T> sender(String address, DeliveryOptions options) {
        return eventBus.sender(address, options);
    }

    @Override
    public <T> MessageProducer<T> publisher(String address) {
        return eventBus.publisher(address);
    }

    @Override
    public <T> MessageProducer<T> publisher(String address, DeliveryOptions options) {
        return eventBus.publisher(address, options);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public EventBus registerCodec(MessageCodec codec) {
        return eventBus.registerCodec(codec);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public EventBus unregisterCodec(String name) {
        return eventBus.unregisterCodec(name);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public <T> EventBus registerDefaultCodec(Class<T> clazz, MessageCodec<T, ?> codec) {
        return eventBus.registerDefaultCodec(clazz, codec);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public EventBus unregisterDefaultCodec(Class clazz) {
        return eventBus.unregisterDefaultCodec(clazz);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void start(Handler<AsyncResult<Void>> completionHandler) {
        eventBus.start(completionHandler);
    }

    @Override
    @io.vertx.codegen.annotations.GenIgnore
    public void close(Handler<AsyncResult<Void>> completionHandler) {
        eventBus.close(completionHandler);
    }

    @Override
    public EventBus addInterceptor(Handler<SendContext> interceptor) {
        return eventBus.addInterceptor(interceptor);
    }

    @Override
    public EventBus removeInterceptor(Handler<SendContext> interceptor) {
        return eventBus.removeInterceptor(interceptor);
    }

    @Override
    public boolean isMetricsEnabled() {
        return eventBus.isMetricsEnabled();
    }
}
