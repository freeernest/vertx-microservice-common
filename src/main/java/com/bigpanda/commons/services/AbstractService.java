package com.bigpanda.commons.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Created by erik on 7/12/18.
 */
//TODO: Refactor all uses of this util to be with static calls instead of inheriting from it
public class AbstractService {
    private static Logger logger = LoggerFactory.getLogger(AbstractService.class);

    /**
     * Used to wrap the resultHandler with logging
     * @param resultHandler
     * @param <R>
     * @return wrapped resultHandler
     */
    public static <R> Handler<AsyncResult<R>> resultHandler(Handler<AsyncResult<R>> resultHandler) {
        return resultHandler(resultHandler, null);
    }

    /**
     * Used to wrap the resultHandler with logging
     * @param resultHandler
     * @param successHandler
     * @param <R>
     * @return wrapped resultHandler
     */
    public static <R, T> Handler<AsyncResult<R>> resultHandler(Handler<AsyncResult<T>> resultHandler, Handler<R> successHandler) {
        return resultHandler(resultHandler, successHandler, null);
    }

    /**
     * Used to wrap the resultHandler with logging
     * @param resultHandler
     * @param successHandler
     * @param <R>
     * @return wrapped resultHandler
     */
    public static <R, T> Handler<AsyncResult<R>> resultHandler(Handler<AsyncResult<T>> resultHandler, Handler<R> successHandler, Handler<R> failHandler) {
        return ar -> {
            if (ar.succeeded()) {
                if (successHandler == null) {
                    resultHandler.handle(Future.succeededFuture((T) ar.result()));
                } else {
                    successHandler.handle(ar.result());
                }
            } else {
                logException(ar.cause());

                if (failHandler == null) {
                    resultHandler.handle(Future.failedFuture(ar.cause()));
                } else {
                    failHandler.handle(ar.result());
                }
            }
        };
    }

    public static <R> void logException(Throwable cause) {
        if (cause instanceof ServiceException) {
            logger.info("ServiceException has thrown: " + cause.getMessage());
        } else {
            logger.error("An error", cause);
        }
    }


    public static <R, T> Handler<AsyncResult<R>> resultHandlerFunction(Handler<AsyncResult<T>> resultHandler, Function<R, T> successHandler) {
        return resultHandler(resultHandler, ar -> {
            resultHandler.handle(Future.succeededFuture(successHandler.apply(ar)));
        });
    }
}
