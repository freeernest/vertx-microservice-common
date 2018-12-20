package com.bigpanda.commons.web.auth;

import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextDecorator;

public class RoutingContextWrapper extends RoutingContextDecorator {
    private final Handler<Void> nextHandler;

    public RoutingContextWrapper(Route currentRoute, RoutingContext decoratedContext, Handler<Void> nextHandler) {
        super(currentRoute, decoratedContext);
        this.nextHandler = nextHandler;
    }

    @Override
    public void next() {
        nextHandler.handle(null);
    }
}
