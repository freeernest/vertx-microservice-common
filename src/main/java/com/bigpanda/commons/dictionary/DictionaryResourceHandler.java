package com.bigpanda.commons.dictionary;

import com.bigpanda.commons.web.helpers.ResponseHelper;
import com.bigpanda.commons.web.resourcehandlers.AbstractResourceHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by erik on 9/6/17.
 */
public class DictionaryResourceHandler extends AbstractResourceHandler {

    private DictionaryService dictionaryService;

    @Override
    public Router createRouter(Vertx vertx) {

        Router router = Router.router(vertx);

        router.get("/").handler(this::handleListDictionaries);
        dictionaryService.getDictionaries().forEach(dictionary -> {
            router.get("/" + dictionary).handler(routingContext -> handleListDictionary(routingContext, dictionary));
        });

        return router;
    }

    private void handleListDictionary(RoutingContext routingContext, String dictionary) {
        ResponseHelper.sendOkay(routingContext.response()
                , dictionaryService.listDictionary(dictionary)
                , 200);
    }

    private void handleListDictionaries(RoutingContext routingContext) {
        ResponseHelper.sendOkay(routingContext.response()
                , dictionaryService.getDictionaries()
                , 200);
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
