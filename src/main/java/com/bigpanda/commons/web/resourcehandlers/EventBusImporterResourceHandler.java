package com.bigpanda.commons.web.resourcehandlers;

import com.bigpanda.commons.json.JsonConversion;
import com.bigpanda.commons.web.helpers.ResponseHelper;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by erik on 2/8/18.
 */
public class EventBusImporterResourceHandler extends AbstractResourceHandler {
    private Vertx vertx;

    @Override
    public Router createRouter(Vertx vertx) {
        this.vertx = vertx;
        Router router = Router.router(vertx);

        // Create account
        router.post("/").handler(this::handleCreateEvent);

        return router;
    }

    private void handleCreateEvent(RoutingContext routingContext) {
        String destAddr = routingContext.request().getHeader("destAddr");
        JsonObject headers = new JsonObject(routingContext.request().getHeader("headers"));
        JsonObject message = routingContext.getBodyAsJson();

        MultiMap multiMap = JsonConversion.toMultiMap(headers);

        if (multiMap.contains("action")) {

            vertx.eventBus().send(destAddr, message, new DeliveryOptions().setHeaders(multiMap), event -> {
                if (event.succeeded()) {
                    ResponseHelper.sendOkay(routingContext.response(), event.result().body(), 200);
                } else {
                    ResponseHelper.sendError(routingContext.response(), event.cause());
                }
            });
            return;
        }

        vertx.eventBus().publish(destAddr, message, new DeliveryOptions().setHeaders(multiMap));

        ResponseHelper.sendOkay(routingContext.response(), "OK", 200);
    }
}
