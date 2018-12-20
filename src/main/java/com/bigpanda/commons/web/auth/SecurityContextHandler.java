package com.bigpanda.commons.web.auth;

import com.bigpanda.commons.services.proxy.ServiceContextHolder;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

public class SecurityContextHandler implements Handler<RoutingContext> {

    public static SecurityContextHandler create() {
        return new SecurityContextHandler();
    }

    SecurityContextHandler() {
    }

    @Override
    public void handle(RoutingContext routingContext) {

        ServiceContextHolder.createNewContext(routingContext.request().getHeader(HttpHeaders.AUTHORIZATION));

        routingContext.next();

        ServiceContextHolder.clean();
    }
}
