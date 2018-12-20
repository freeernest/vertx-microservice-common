package com.bigpanda.commons.async;

import com.bigpanda.commons.services.AbstractService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class FutureUtil {

    public static final String NO_HANDLERS_FOR_ADDRESS_PREFIX = "No handlers for address";
    private static Logger logger = LoggerFactory.getLogger(FutureUtil.class);

    public static <U> Future<U> tryUntilHandlersAvailable(Vertx vertx, Function<Void, Future<U>> mapper) {

        return tryUntilHandlersAvailable(vertx, mapper, 10000);
    }

    public static <U> Future<U> tryUntilHandlersAvailable(Vertx vertx, Function<Void, Future<U>> mapper, long attemptDelay) {
        Future<U> future = Future.future();
        attemptUntilHandlersAvailable(vertx, mapper, future, attemptDelay);

        return future;
    }

    private static <U> void attemptUntilHandlersAvailable(Vertx vertx, Function<Void, Future<U>> mapper, Future<U> future, long attemptDelay) {
        mapper.apply(null).setHandler(ar -> {
            if (ar.failed()) {

                if (ar.cause() instanceof ReplyException
                        && ar.cause().getMessage() != null
                        && ar.cause().getMessage().startsWith(NO_HANDLERS_FOR_ADDRESS_PREFIX)) {

                    logger.error("Retrying to get {}", ar.cause().getMessage().substring(NO_HANDLERS_FOR_ADDRESS_PREFIX.length() + 1));

                    vertx.setTimer(attemptDelay, timer -> attemptUntilHandlersAvailable(vertx, mapper, future, attemptDelay));
                } else {

                    future.fail(ar.cause());
                }


            } else {
                future.complete(ar.result());
            }
        });
    }

    public static <U,T> Future<T> toFuture(Consumer<Handler<AsyncResult<U>>> consumer, Function<U,T> converter) {
        Future<U> future = toFuture(consumer);

        return convert(future, converter);
    }

    public static <U> Future<U> toFuture(Consumer<Handler<AsyncResult<U>>> consumer) {
        Future<U> future = Future.future();

        consumer.accept(future.completer());

        return future;
    }

    public static <U,T> Future<T> convert(Future<U> future, Function<U,T> converter) {
        Future<T> resFuture = Future.future();

        future.setHandler(res -> {
            if (res.succeeded()) {
                resFuture.complete(converter.apply(res.result()));
            }
            else {
                resFuture.fail(res.cause());
            }
        });

        return resFuture;
    }

    public static <U> void toAsyncResult(Future<U> future, Handler<AsyncResult<U>> resultHandler) {
        toAsyncResult(future, resultHandler, res -> res);
    }

    public static <U, T> void toAsyncResult(Future<U> future, Handler<AsyncResult<T>> resultHandler, Function<U,T> converter) {
        future.setHandler(AbstractService.resultHandlerFunction(resultHandler, converter));
    }

    public static <U> Future<U> toFutureWithFail(Consumer<Handler<AsyncResult<U>>> consumer, Handler<Void> failHandler) {
        return toFutureWithFail(toFuture(consumer), failHandler);
    }

    public static <U> Future<U> toFutureWithFail(Future<U> future, Handler<Void> failHandler) {
        Future<U> resFuture = Future.future();

        future.setHandler(res -> {
            if (res.succeeded()) {
                resFuture.complete(res.result());
            }
            else {
                try {
                    failHandler.handle(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resFuture.fail(res.cause());
            }
        });

        return resFuture;
    }

    public static <U,T> Future<U> toFutureWithFail(Consumer<Handler<AsyncResult<U>>> consumer, Consumer<Handler<AsyncResult<T>>> failConsumer, boolean isSyncFailConsumer) {
        return toFutureWithFail(toFuture(consumer), failConsumer, isSyncFailConsumer);
    }

    public static <U,T> Future<U> toFutureWithFail(Future<U> future, Consumer<Handler<AsyncResult<T>>> failConsumer, boolean isSyncFailConsumer) {
        Future<U> resFuture = Future.future();

        future.setHandler(res -> {
            if (res.succeeded()) {
                resFuture.complete(res.result());
            }
            else {
                try {
                    Future<T> failFuture = toFuture(failConsumer);

                    if (isSyncFailConsumer) {
                        failFuture.setHandler(failFutureRes -> resFuture.fail(res.cause()));
                    }
                    else {
                        resFuture.fail(res.cause());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return resFuture;
    }
}
