package com.bigpanda.commons.json.refs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bigpanda.commons.json.refs.JsonRefHelper.retrieveJsonObject;

/**
 * Created by erik on 10/25/17.
 */
class JsonRefsResolverImpl implements JsonRefsResolver {

    private JsonRefResolver jsonRefResolver;

    public JsonRefsResolverImpl(Vertx vertx) {
        jsonRefResolver = JsonRefResolver.create(vertx);
    }

    @Override
    public void resolve(JsonObject object, String configLocation, Handler<AsyncResult<JsonObject>> resultHandler) {

        List<JsonRef> refs = new ArrayList<>();
        findRefs(object, refs, "root");

        if (refs.size() > 0) {
            resolveRefs(object, refs, configLocation, 0, resultHandler);
        } else {
            resultHandler.handle(Future.succeededFuture(object));
        }
    }

    private void resolveRefs(JsonObject object, List<JsonRef> refs, String configLocation, int index, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject refObject = retrieveJsonObject(object, refs.get(index).getPath());

        jsonRefResolver.resolve(object, refObject, configLocation, event -> {

            if (refs.size() > index + 1) {
                resolveRefs(object, refs, configLocation, index + 1, resultHandler);
            } else {
                resultHandler.handle(Future.succeededFuture(object));
            }
        });
    }


    private void findRefs(JsonObject object, List<JsonRef> refs, String path) {
        for (Map.Entry<String, Object> s : object.getMap().entrySet()) {
            if (s.getValue() instanceof String) {
                if (s.getKey().equals("$ref")) {
                    refs.add(new JsonRef(path));
                }
            } else if (s.getValue() instanceof Map) {
                findRefs(new JsonObject((Map<String, Object>) s.getValue()), refs, path + "/" + s.getKey());
            }
        }
    }

}
