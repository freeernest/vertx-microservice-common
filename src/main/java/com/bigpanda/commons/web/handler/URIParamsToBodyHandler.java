package com.bigpanda.commons.web.handler;

import com.bigpanda.commons.json.JsonConversion;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class URIParamsToBodyHandler implements Handler<RoutingContext> {

    public static URIParamsToBodyHandler create() {
        return new URIParamsToBodyHandler();
    }

    URIParamsToBodyHandler() {
    }

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.setBody(
                routingContext.getBodyAsJson().mergeIn(
                        JsonConversion.fromMultiMap(routingContext.request().params())
                ).toBuffer()
        );

        routingContext.next();
    }
}
