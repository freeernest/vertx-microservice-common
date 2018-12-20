package com.bigpanda.commons.verticles;

import io.vertx.core.json.JsonObject;

public class KafkaJsonSenderVerticle extends KafkaSenderVerticle<JsonObject> {

    @Override
    protected String getMessage(JsonObject message) {
        return message.encode();
    }
}
