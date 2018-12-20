package com.bigpanda.commons.flows.status;

import com.bigpanda.commons.services.AbstractService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Created by erik on 9/13/18.
 */
public class StatusBehaviour extends AbstractService {
    private final Map<String, StatusFunction> statusFunctions;

    public StatusBehaviour(Map<String, StatusFunction> statusFunctions) {
        this.statusFunctions = statusFunctions;
    }

    public Future<Boolean> isStatusAllowed(String status, JsonObject object) {
        Future<Boolean> future = Future.future();

        if(!statusFunctions.containsKey(status)) {
            future.complete(false);
        }
        else {
            StatusFunction statusFunction = statusFunctions.get(status);

            if (statusFunction == null) {
                future.complete(true);
            }
            else {
                future = statusFunctions.get(status).isStatusAllowed(object);
            }
        }

        return future;
    }

    public Future<Void> applyStatus(String status, JsonObject object) {
        StatusFunction function = statusFunctions.get(status);
        if (function != null) {
            return function.apply(object);
        }

        return Future.succeededFuture();
    }

    public Future<Void> rollbackStatus(String status, JsonObject object) {
        StatusFunction function = statusFunctions.get(status);
        if (function != null) {
            return function.rollback(object);
        }

        return Future.succeededFuture();
    }
}
