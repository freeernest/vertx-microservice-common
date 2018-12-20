package com.bigpanda.commons.services;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class SqlModelRepositoryWrapper extends SqlRepositoryWrapper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected List<String> createModel;
    protected List<String> fetchModel;
    protected List<String> updateModel;

    public SqlModelRepositoryWrapper(Vertx vertx) {
        super();

        vertx.fileSystem().readFile("models/"+getClass().getSimpleName().replace("Impl", "")+ ".model.json", event -> {
            if (event.succeeded()) {

                createModel = new ArrayList<>();
                fetchModel = new ArrayList<>();
                updateModel = new ArrayList<>();

                JsonObject object = new JsonObject(event.result().toString());

                object.forEach(stringObjectEntry -> {
                    JsonArray operations = ((JsonArray) stringObjectEntry.getValue());
                    if (operations.contains("create")) {
                        createModel.add(stringObjectEntry.getKey());
                    }
                    if (operations.contains("fetch")) {
                        fetchModel.add(stringObjectEntry.getKey());
                    }
                    if (operations.contains("update")) {
                        updateModel.add(stringObjectEntry.getKey());
                    }
                });
            } else {
                logger.error("error while loading model", event.cause());
            }
        });
    }
}
