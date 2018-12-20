package com.bigpanda.commons.web.handler;

import com.bigpanda.commons.json.JsonConversion;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class QueryString2BodyHandler implements Handler<RoutingContext> {

    public static QueryString2BodyHandler create() {
        return new QueryString2BodyHandler();
    }

    QueryString2BodyHandler() {
    }

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.setBody(
                JsonConversion.fromMultiMap(routingContext.request().params()).toBuffer()
        );

        routingContext.next();
    }
}
