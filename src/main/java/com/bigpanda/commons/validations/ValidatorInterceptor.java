package com.bigpanda.commons.validations;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public interface ValidatorInterceptor extends Function<Message<JsonObject>, Future<Message<JsonObject>>> {
    ValidatorInterceptor init(Vertx vertx, Class clazz);
}
