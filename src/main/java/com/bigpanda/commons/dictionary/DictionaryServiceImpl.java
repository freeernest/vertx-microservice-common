package com.bigpanda.commons.dictionary;

import com.bigpanda.commons.services.SqlRepositoryWrapper;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryServiceImpl extends SqlRepositoryWrapper implements DictionaryService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> dictionaries;
    private Map<String, List<JsonObject>> dictionariesValues;
    private Vertx vertx;

    public void init() {
        dictionariesValues = new ConcurrentHashMap<>();

        load();
        vertx.setTimer(1000 * 300, event -> {
            load();
        });
    }

    @Override
    public List<JsonObject> listDictionary(String name) {

        if (!dictionaries.contains(name)) {
            throw new ServiceException(700, "Dictionary does not exist");
        }

        return dictionariesValues.get(name);
    }

    @Override
    public boolean isValidValue(String dictionary, String value) {
        List<JsonObject> list = listDictionary(dictionary);
        for (JsonObject object : list) {
            if (value.equals(object.getString("name"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getDictionaries() {
        return dictionaries;
    }

    private void load() {
        dictionaries.forEach(dictionary -> {
            retrieveAll("select * from d_" + dictionary.toLowerCase()).setHandler(result -> {
                if (result.failed()) {
                    logger.error("An Error", result.cause());
                    return;
                }
                dictionariesValues.put(dictionary, result.result());
            });
        });
    }

    public void setDictionaries(List<String> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }
}
