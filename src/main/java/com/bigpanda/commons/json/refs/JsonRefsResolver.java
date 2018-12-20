package com.bigpanda.commons.json.refs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by erik on 10/26/17.
 */
public interface JsonRefsResolver {

    static JsonRefsResolver create(Vertx vertx) {
        return new JsonRefsResolverImpl(vertx);
    }

    /**
     * Resolve all refs inside a given JsonObject, this changes the given object too
     * @param object - JsonObject
     * @param configLocation - Used in order to resolve file refs in relative path
     * @param resultHandler - AsyncResult
     */
    void resolve(JsonObject object, String configLocation, Handler<AsyncResult<JsonObject>> resultHandler);
}
