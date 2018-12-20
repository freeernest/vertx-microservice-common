package com.bigpanda.commons.json.refs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;

import static com.bigpanda.commons.json.refs.JsonRefHelper.retrieveJsonObject;

/**
 * Created by erik on 10/26/17.
 */
class JsonRefResolverImpl implements JsonRefResolver {

    private final Vertx vertx;
    private final HttpClient httpClient;

    public JsonRefResolverImpl(Vertx vertx) {
        this.vertx = vertx;
        this.httpClient = vertx.createHttpClient(new HttpClientOptions().setTrustAll(true));
    }

    @Override
    public void resolve(JsonObject object, JsonObject refObject, String configLocation, Handler<AsyncResult> handler) {
        String refValue = refObject.getString("$ref");
        refObject.remove("$ref");

        if (refValue.startsWith("#")) {

            resolveLocalRef(object, refObject, refValue, handler);
        } else if (refValue.startsWith("file")) {

            resolveFileRef(refObject, refValue, handler, configLocation);
        } else if (refValue.startsWith("http")) {

            resolveHttpRef(refObject, refValue, handler);
        } else {

            handler.handle(Future.failedFuture("Cannot resolve ref " + refValue));
        }
    }


    private void resolveHttpRef(JsonObject refObject, String refValue, Handler<AsyncResult> handler) {

        httpClient.getAbs(refValue, responseHandler -> {

            if (responseHandler.statusCode() == 200) {

                StringBuilder builder = new StringBuilder();

                responseHandler.handler(event  -> {
                    builder.append(event.toString());
                });
                responseHandler.endHandler(event -> {
                    JsonObject resolvedObject = new JsonObject(builder.toString());
                    resolvedObject.forEach(stringObjectEntry -> refObject.put(stringObjectEntry.getKey(), stringObjectEntry.getValue()));

                    handler.handle(Future.succeededFuture());
                });

                responseHandler.exceptionHandler(event -> {
                    handler.handle(Future.failedFuture(event));
                });
            } else {
                handler.handle(Future.failedFuture(responseHandler.statusMessage()));
            }
        }).end();
    }

    private void resolveFileRef(JsonObject refObject, String refValue, Handler<AsyncResult> handler, String configLocation) {
        String filePath = refValue.replace("file:", "");
        String baseLocation = getBaseLocation(configLocation);
        if (!filePath.startsWith("/") && baseLocation != null) {
            filePath = baseLocation + filePath;
        }
        vertx.fileSystem().readFile(filePath, event -> {
            if (event.succeeded()) {
                JsonObject resolvedObject = new JsonObject(event.result().toString());
                resolvedObject.forEach(stringObjectEntry -> refObject.put(stringObjectEntry.getKey(), stringObjectEntry.getValue()));

                handler.handle(Future.succeededFuture());
            } else {
                handler.handle(Future.failedFuture(event.cause()));
            }
        });
    }

    private void resolveLocalRef(JsonObject object, JsonObject refObject, String refValue, Handler<AsyncResult> handler) {
        JsonObject resolvedObject = retrieveJsonObject(object, refValue.replace("#", "root"));
        resolvedObject.forEach(stringObjectEntry -> refObject.put(stringObjectEntry.getKey(), stringObjectEntry.getValue()));

        handler.handle(Future.succeededFuture());
    }

    private String getBaseLocation(String location) {
        if (location != null) {
            int index = location.lastIndexOf("/");
            if (index != -1) {
                return location.substring(0, index) + "/";
            }
        }
        return null;
    }
}
