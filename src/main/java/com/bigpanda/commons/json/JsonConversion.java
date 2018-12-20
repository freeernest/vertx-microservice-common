package com.bigpanda.commons.json;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JsonConversion {

    public static Map<String, String> toStringMap(JsonObject object) {
        Map<String, String> map = new HashMap<>(object.size());
        object.forEach(e -> map.put(e.getKey(), e.getValue().toString()));
        return map;
    }

    public static JsonObject fromMultiMap(MultiMap multiMap) {
        JsonObject object = new JsonObject();
        multiMap.forEach(stringStringEntry -> {
            Object o = object.getValue(stringStringEntry.getKey());

            if (o != null) {

                if (o instanceof JsonArray) {
                    ((JsonArray) o).add(stringStringEntry.getValue());
                } else {
                    object.put(stringStringEntry.getKey(), new JsonArray().add(o.toString()).add(stringStringEntry.getValue()));
                }
            } else {
                object.put(stringStringEntry.getKey(), stringStringEntry.getValue());
            }

        });
        return object;
    }

    public static MultiMap toMultiMap(JsonObject jsonObject) {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();

        jsonObject.forEach(stringObjectEntry -> {
            Object o = stringObjectEntry.getValue();

            if (o != null) {

                if (o instanceof JsonArray) {

                    ((JsonArray) o).forEach( a -> multiMap.add(stringObjectEntry.getKey(), a.toString()));
                } else {
                    multiMap.add(stringObjectEntry.getKey(), o.toString());
                }
            }

        });
        return multiMap;
    }
}
