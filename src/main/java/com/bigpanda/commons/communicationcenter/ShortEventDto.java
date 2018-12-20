package com.bigpanda.commons.communicationcenter;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class ShortEventDto extends com.techfinancials.microservices.ccbridge.dto.ShortEventDto {

    // Mandatory for data objects
    public ShortEventDto(JsonObject jsonObject) {
        ShortEventDtoConverter.fromJson(jsonObject, this);
    }

    public ShortEventDto() {
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ShortEventDtoConverter.toJson(this, json);
        return json;
    }
}
