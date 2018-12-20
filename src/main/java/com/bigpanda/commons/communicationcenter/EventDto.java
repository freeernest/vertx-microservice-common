package com.bigpanda.commons.communicationcenter;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class EventDto extends com.techfinancials.microservices.ccbridge.dto.EventDto {

    // Mandatory for data objects
    public EventDto(JsonObject jsonObject) {
        EventDtoConverter.fromJson(jsonObject, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        EventDtoConverter.toJson(this, json);
        return json;
    }
}
