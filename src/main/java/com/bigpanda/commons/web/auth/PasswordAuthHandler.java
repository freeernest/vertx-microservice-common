package com.bigpanda.commons.web.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

import java.util.List;
import java.util.Set;

public class PasswordAuthHandler implements JWTAuthHandler {

    public static final String PASSWORD_AUTH = "password.auth";
    private final Vertx vertx;
    private final JWTAuthHandler jwtAuthHandler;

    public static PasswordAuthHandler create(Vertx vertx, JWTAuth jwtAuth) {
        return new PasswordAuthHandler(vertx, jwtAuth);
    }

    private PasswordAuthHandler(Vertx vertx, JWTAuth jwtAuth) {
        this.vertx = vertx;
        this.jwtAuthHandler = JWTAuthHandler.create(jwtAuth);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        jwtAuthHandler.handle(new RoutingContextWrapper(routingContext.currentRoute(), routingContext, res -> {
            vertx.eventBus().send(PASSWORD_AUTH, routingContext.user().principal(), passRes -> {
                if (passRes.succeeded()) {
                    routingContext.next();
                } else {
                    routingContext.fail(401);
                }
            });
        }));
    }

    @Override
    public JWTAuthHandler setAudience(List<String> audience) {
        jwtAuthHandler.setAudience(audience);
        return this;
    }

    @Override
    public JWTAuthHandler setIssuer(String issuer) {
        jwtAuthHandler.setIssuer(issuer);
        return this;
    }

    @Override
    public JWTAuthHandler setIgnoreExpiration(boolean ignoreExpiration) {
        jwtAuthHandler.setIgnoreExpiration(ignoreExpiration);
        return this;
    }

    @Override
    public AuthHandler addAuthority(String authority) {
        jwtAuthHandler.addAuthority(authority);
        return this;
    }

    @Override
    public AuthHandler addAuthorities(Set<String> authorities) {
        jwtAuthHandler.addAuthorities(authorities);
        return this;
    }

    @Override
    public void parseCredentials(RoutingContext routingContext, Handler<AsyncResult<JsonObject>> handler) {
        jwtAuthHandler.parseCredentials(routingContext, handler);
    }

    @Override
    public void authorize(User user, Handler<AsyncResult<Void>> handler) {
        jwtAuthHandler.authorize(user, handler);
    }

}
