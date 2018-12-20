package com.bigpanda.commons.sql.helpers;

import io.vertx.core.json.JsonObject;

/**
 * Created by erik on 5/27/18.
 */
public class PaginationHelper {

    public static boolean isCountRequest(JsonObject searchCriteria) {
        return searchCriteria.containsKey("p_count");
    }
}
