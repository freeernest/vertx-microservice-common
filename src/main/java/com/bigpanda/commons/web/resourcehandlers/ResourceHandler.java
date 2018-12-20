package com.bigpanda.commons.web.resourcehandlers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Created by erik on 9/6/17.
 */
public interface ResourceHandler {
    Router createRouter(Vertx vertx);
}
