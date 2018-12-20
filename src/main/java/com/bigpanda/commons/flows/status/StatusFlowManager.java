package com.bigpanda.commons.flows.status;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Created by erik on 9/13/18.
 */
public interface StatusFlowManager {

    Future<Boolean> isStatusChangeAllowed(String oldStatus, String newStatus, JsonObject object);

    Future<Void> applyStatusChange(String oldStatus, String newStatus, JsonObject object);

    Future<Void> rollbackStatusChange(String oldStatus, String newStatus, JsonObject object);
}
