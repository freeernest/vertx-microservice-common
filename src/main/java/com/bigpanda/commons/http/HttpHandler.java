package com.bigpanda.commons.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.impl.NoStackTraceThrowable;

/**
 * Created by erik on 11/14/17.
 */
public class HttpHandler {

    public static Handler<HttpClientResponse> responseHandler(Handler<AsyncResult<String>> handler) {
        return responseHandler -> {

            StringBuilder builder = new StringBuilder();

            responseHandler.handler(event  -> builder.append(event.toString()));

            responseHandler.endHandler(event -> {
                if (responseHandler.statusCode() >= 200 && responseHandler.statusCode() < 300) {
                    handler.handle(Future.succeededFuture(builder.toString()));
                } else {
                    String message = "HTTP status: %d(%s), body: %s";
                    handler.handle(Future.failedFuture(new NoStackTraceThrowable(String.format(message, responseHandler.statusCode(), responseHandler.statusMessage(), builder.toString()))));
                }
            });

            responseHandler.exceptionHandler(event -> handler.handle(Future.failedFuture(event)));
        };
    }
}
