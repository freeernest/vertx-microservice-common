package com.bigpanda.commons.web.resourcehandlers;

import com.bigpanda.commons.web.helpers.ResponseHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

/**
 * Created by erik on 11/13/17.
 */
public abstract class AbstractResourceHandler implements ResourceHandler {

    /**
     * This method generates handler for async methods in REST APIs.
     */
    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
        return res -> {
            if (res.succeeded()) {
                handler.handle(res.result());
            } else {
                ResponseHelper.sendError(context.response(), res.cause());
            }
        };
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * Use the result directly and put as 'result' as the response. The content type is JSON.
     */
    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, int statusCode) {
        return res -> {
            if (res.succeeded()) {
                ResponseHelper.sendOkay(context.response(), res.result(), statusCode);
            } else {
                ResponseHelper.sendError(context.response(), res.cause());
            }
        };
    }

    protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context) {
        return resultHandler(context, 200);
    }

    /**
     * This method generates handler for async methods in REST APIs.
     * Apply the converter on the result directly and put as 'result' as the response. The content type is JSON.
     */
    protected <T> Handler<AsyncResult<T>> resultConvertHandler(RoutingContext context, Function<T, T> converter, int statusCode) {
        return res -> {
            if (res.succeeded()) {
                ResponseHelper.sendOkay(context.response(), converter.apply(res.result()), statusCode);
            } else {
                ResponseHelper.sendError(context.response(), res.cause());
            }
        };
    }

    protected <T> Handler<AsyncResult<T>> resultConvertHandler(RoutingContext context, Function<T, T> converter) {
        return resultConvertHandler(context, converter, 200);
    }
}
