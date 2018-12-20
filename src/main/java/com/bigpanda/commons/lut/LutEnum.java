package com.bigpanda.commons.lut;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface LutEnum<T> {
    T getValue();

    LutEnum get(T value);

    static <T extends Enum<T>> void replaceFieldWithLutName(JsonObject o, Class<T> lutEnum, String field) {
        if (o.containsKey(field)) {
            Integer fieldValue = o.getInteger(field);
            Object lutName = ((LutEnum) lutEnum.getEnumConstants()[0]).get(fieldValue);
            if (lutName != null)
                o.put(field, lutName.toString());
        }
    }

    static <T extends Enum<T>> void replaceFieldWithLutId(JsonObject o, Class<T> lutEnum, String field) {
        if (o.containsKey(field)) {
            String fieldValue = o.getString(field);
            o.put(field, ((LutEnum) Enum.valueOf(lutEnum, fieldValue)).getValue());
        }
    }

    static List allowedValues(Class<? extends Enum> lutEnum) {
        return Arrays.asList(lutEnum.getEnumConstants())
                .stream()
                .map(o -> ((LutEnum) o).getValue() )
                .collect(Collectors.toList());
    }
}
