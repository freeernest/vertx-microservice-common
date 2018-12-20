package com.bigpanda.commons.json;

import io.vertx.core.json.JsonObject;

public class JsonComparison {

    public final static String PREV_FIELD_SUFFIX = "-prev";
    /**
     * Return JsonObject with updated fields including changed fields according to prev JsonObject
     * The changed fields will be returned as with suffix '-prev'.
     * Ex, 'status' field that changed will be returned with 'status-prev' that indicate the previous value of 'status'
     * @param updated
     * @param prev
     * @return
     */
    public static JsonObject compare(JsonObject updated, JsonObject prev) {
        JsonObject changedStore = new JsonObject();
        updated.forEach(stringObjectEntry -> {
            Object prevValue = prev.getValue(stringObjectEntry.getKey());
            Object updatedValue = stringObjectEntry.getValue();
            if (prevValue == null && updatedValue != null) {

                changedStore.put(stringObjectEntry.getKey() + PREV_FIELD_SUFFIX, prevValue);

            } else if (prevValue != null && updatedValue != null && !prevValue.toString().equals(updatedValue.toString())) {

                changedStore.put(stringObjectEntry.getKey() + PREV_FIELD_SUFFIX, prevValue);
            }
        });

        return updated.copy().mergeIn(changedStore);
    }

    public static Object compareField(JsonObject updated, JsonObject prev, String fieldName) {
        Object value = updated.getValue(fieldName);

        return value != null && !value.equals(prev.getInteger(fieldName)) ? value : null;
    }
}
