package com.bigpanda.commons.json.refs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by erik on 10/26/17.
 */
public interface JsonRefResolver {

    static JsonRefResolver create(Vertx vertx) {
        return new JsonRefResolverImpl(vertx);
    }

    void resolve(JsonObject object, JsonObject refObject, String configLocation, Handler<AsyncResult> handler);
}
