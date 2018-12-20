package com.bigpanda.commons.web.auth;

import com.bigpanda.commons.security.ForbiddenException;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;


public class JWTAuthHandler implements Handler<RoutingContext> {

    private final io.vertx.ext.web.handler.JWTAuthHandler jwtAuthHandler;
    private final String authority;

    public static JWTAuthHandler create(JWTAuth jwtAuth, String authority) {
        return new JWTAuthHandler(jwtAuth, authority);
    }

    private JWTAuthHandler(JWTAuth jwtAuth, String authority) {
        this.jwtAuthHandler = io.vertx.ext.web.handler.JWTAuthHandler.create(jwtAuth);
        this.authority = authority;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        jwtAuthHandler.handle(new RoutingContextWrapper(routingContext.currentRoute(), routingContext, res -> {
            JsonArray userAuthorities = routingContext.user().principal().getJsonArray("auths");
            if (userAuthorities.contains(authority)) {
                routingContext.next();
            } else {
                throw new ForbiddenException();
            }
        }));
    }
}
