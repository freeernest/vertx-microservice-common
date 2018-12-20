package com.bigpanda.commons.web.verticles;

import com.bigpanda.commons.web.errors.GeneralErrorCode;
import com.bigpanda.commons.web.helpers.ResponseHelper;
import com.bigpanda.commons.web.resourcehandlers.ResourceHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by erik on 7/24/17.
 */
public class HttpVerticle extends AbstractVerticle {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private HttpServer server;
    private HttpServer redirectServer;
    private Map<String, ResourceHandler> resourceHandlers;
    private int port = 8080;
    private int sslPort = 8443;
    private JsonObject options;
    private boolean enableCors = false;

    public void setResourceHandlers(Map<String, ResourceHandler> resourceHandlers) {
        this.resourceHandlers = resourceHandlers;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public void setOptions(JsonObject options) {
        this.options = options;
    }

    public void setEnableCors(boolean enableCors) {
        this.enableCors = enableCors;
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        if (enableCors)
            enableCorsSupport(router);

        router.route().failureHandler(this::handleException);
        router.route().last().handler(this::handleNotFound);

        if (resourceHandlers != null) {
            resourceHandlers.forEach((s, resourceHandler) -> {
                router.mountSubRouter(s, resourceHandler.createRouter(vertx));
            });
        }

        boolean ssl = options == null ? false : options.getBoolean("ssl", false);

        server = vertx.createHttpServer(options == null ? new HttpServerOptions() : new HttpServerOptions(options))
                .requestHandler(router::accept)
                .listen(ssl ? sslPort : port);

        if (ssl) {
            redirectServer = vertx.createHttpServer()
                    .requestHandler(request -> {
                        String location = request.scheme() + "s://" + request.host().replace(":" + port, ":" + sslPort) + request.uri();
                        request.response().putHeader("location", location).setStatusCode(302).end();
                    })
                    .listen(port);
        }
    }

    private void handleException(RoutingContext routingContext) {
        if (routingContext.statusCode() == 401) {
            ResponseHelper.sendError(routingContext.response(), 401, "Unauthorized", GeneralErrorCode.UNAUTHORIZED);
            return;
        }

        if (routingContext.failure() != null) {
            if (routingContext.failure() instanceof ServiceException) {
                ResponseHelper.sendError(routingContext.response(), routingContext.failure());
                return;
            } else if (routingContext.failure() instanceof DecodeException) {
                ResponseHelper.sendError(routingContext.response(), 400, routingContext.failure().getMessage(), GeneralErrorCode.DECODE_ERROR);
                return;
            } else {
                ResponseHelper.sendError(routingContext.response(), 500, "Exception on server", GeneralErrorCode.GENERAL_ERROR);
                logger.error("An error", routingContext.failure());
                return;
            }
        }

        routingContext.next();
    }

    private void handleNotFound(RoutingContext routingContext) {
        ResponseHelper.sendError(routingContext.response(), 404, "Resource not found", GeneralErrorCode.RESOURCE_NOT_FOUND);
    }

    protected void enableCorsSupport(Router router) {
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        allowHeaders.add("auth-token");
        allowHeaders.add("Authorization");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.PUT);
        allowMethods.add(HttpMethod.OPTIONS);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));
    }

    @Override
    public void stop() throws Exception {
        server.close();
        if (redirectServer != null)
            redirectServer.close();
    }
}
