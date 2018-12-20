package com.bigpanda.commons.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by erik on 3/14/18.
 */
public class SafeAsyncResult {

    private static Logger logger = LoggerFactory.getLogger(SafeAsyncResult.class.getClass());

    public static <T> Handler<AsyncResult<T>> async(Handler<AsyncResult<T>> handler) {
        return res -> {
            if (res.succeeded()) {
                try {
                    handler.handle(Future.succeededFuture(res.result()));
                } catch (Exception e) {
                    handler.handle(Future.failedFuture(e));
                    logger.error("Unhandled async exception", e);
                }
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        };
    }
}
