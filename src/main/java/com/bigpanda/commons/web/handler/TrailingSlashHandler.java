package com.bigpanda.commons.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * An handler that forces the URI to have a trailing slash
 * In case the trailing slash does not present in URI it will redirect to
 *
 * Created by erik on 9/10/17.
 */
public class TrailingSlashHandler implements Handler<RoutingContext> {

    public static TrailingSlashHandler create() {
        return new TrailingSlashHandler();
    }

    private TrailingSlashHandler() {

    }

    @Override
    public void handle(RoutingContext event) {
        String routePath = event.mountPoint() + event.currentRoute().getPath();
        if ((event.request().uri() + "/").endsWith(routePath)) {
            event.response().putHeader("location", "." + routePath).setStatusCode(302).end();
            return;
        }
        event.next();
    }
}
