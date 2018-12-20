package com.bigpanda.commons.flows.status;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Created by erik on 9/13/18.
 */
public class StatusFlowManagerImpl implements StatusFlowManager {

    private Map<String, StatusBehaviour> statusBehaviourMap;

    @Override
    public Future<Boolean> isStatusChangeAllowed(String oldStatus, String newStatus, JsonObject object) {
        Future<Boolean> future = Future.future();

        StatusBehaviour statusBehaviour = statusBehaviourMap.get(oldStatus);

        if (statusBehaviour == null) {
            future.complete(false);
        }
        else {
           future = statusBehaviour.isStatusAllowed(newStatus, object);
        }

        return future;
    }

    @Override
    public Future<Void> applyStatusChange(String oldStatus, String newStatus, JsonObject object) {
        if (!statusBehaviourMap.containsKey(oldStatus)) {
            throw new IllegalArgumentException(String.format("Status %s is not mapped on the statusBehaviourMap", oldStatus));
        }
        return statusBehaviourMap.get(oldStatus).applyStatus(newStatus, object);
    }

    @Override
    public Future<Void> rollbackStatusChange(String oldStatus, String newStatus, JsonObject object) {
        if (!statusBehaviourMap.containsKey(oldStatus)) {
            throw new IllegalArgumentException(String.format("Status %s is not mapped on the statusBehaviourMap", oldStatus));
        }
        return statusBehaviourMap.get(oldStatus).rollbackStatus(newStatus, object);
    }

    public void setStatusBehaviourMap(Map<String, StatusBehaviour> statusBehaviourMap) {
        this.statusBehaviourMap = statusBehaviourMap;
    }
}
