package com.bigpanda.commons.web.resourcehandlers;

import com.bigpanda.commons.web.handler.TrailingSlashHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Created by erik on 9/6/17.
 */
public class StaticResourceHandler extends AbstractResourceHandler {

    private String webRoot;
    private boolean cachingEnabled = true;

    public void setWebRoot(String webRoot) {
        this.webRoot = webRoot;
    }

    public void setCachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
    }

    @Override
    public Router createRouter(Vertx vertx) {

        Router router = Router.router(vertx);
        router.route("/").handler(TrailingSlashHandler.create());
        router.route("/*").handler(StaticHandler.create()
                .setAllowRootFileSystemAccess(true)
                .setCachingEnabled(cachingEnabled)
                .setWebRoot(webRoot));

        return router;
    }

}
