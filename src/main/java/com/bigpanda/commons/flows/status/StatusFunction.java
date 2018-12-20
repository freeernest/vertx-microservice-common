package com.bigpanda.commons.flows.status;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Created by erik on 9/16/18.
 */
public interface StatusFunction {
   default Future<Boolean> isStatusAllowed(JsonObject object) { return Future.succeededFuture(true);}
    Future<Void> apply(JsonObject object);
    Future<Void> rollback(JsonObject object);
}
