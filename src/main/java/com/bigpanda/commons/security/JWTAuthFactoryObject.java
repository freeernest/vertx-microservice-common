package com.bigpanda.commons.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import org.springframework.beans.factory.FactoryBean;

public class JWTAuthFactoryObject implements FactoryBean<JWTAuth> {
    private Vertx vertx;
    private JWTAuthOptions jwtAuthOptions;

    @Override
    public JWTAuth getObject() throws Exception {
        return JWTAuth.create(vertx, jwtAuthOptions);
    }

    @Override
    public Class<?> getObjectType() {
        return JWTAuth.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setJwtAuthOptions(JWTAuthOptions jwtAuthOptions) {
        this.jwtAuthOptions = jwtAuthOptions;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }
}
