package com.bigpanda.commons.dictionary;

import io.vertx.core.json.JsonObject;

import java.util.List;

public interface DictionaryService {

    List<JsonObject> listDictionary(String name);

    boolean isValidValue(String dictionary, String value);

    List<String> getDictionaries();
}
