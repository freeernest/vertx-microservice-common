package com.bigpanda.commons.json.refs;

import io.vertx.core.json.JsonObject;

/**
 * Created by erik on 10/26/17.
 */
class JsonRefHelper {

    static JsonObject retrieveJsonObject(JsonObject object, String path) {
        String[] nodes = path.split("/");
        JsonObject returnObject = object;
        for (String node : nodes) {
            if (!node.equals("root")) {
                returnObject = returnObject.getJsonObject(node);
            }
        }
        return returnObject;
    }
}
