package com.bigpanda.commons.web.resourcehandlers;

import io.vertx.ext.auth.jwt.JWTAuth;

/**
 * Created by erik on 9/6/17.
 */
public interface JWTResourceHandler extends ResourceHandler {
    JWTResourceHandler withJWTAuth(JWTAuth jwtAuth);
}
