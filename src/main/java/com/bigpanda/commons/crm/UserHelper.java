package com.bigpanda.commons.crm;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by erik on 3/7/18.
 */
public class UserHelper {

    public static String extractUser(RoutingContext routingContext) {
        if (routingContext.user() == null) {
            return "anonymous";
        }
        return routingContext.user().principal().getString("user");
    }
}
