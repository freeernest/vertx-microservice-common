package com.bigpanda.commons.security;

import io.vertx.serviceproxy.ServiceException;

/**
 * Created by erik on 10/25/17.
 */
public class UnauthorizedException extends ServiceException {

    public static int CODE = UnauthorizedException.class.getName().hashCode();

    public UnauthorizedException() {
        super(CODE, "Unauthorized");
    }
}
