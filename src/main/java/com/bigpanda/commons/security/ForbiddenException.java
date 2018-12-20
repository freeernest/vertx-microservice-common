package com.bigpanda.commons.security;

import io.vertx.serviceproxy.ServiceException;

/**
 * Created by erik on 10/25/17.
 */
public class ForbiddenException extends ServiceException {

    public static int CODE = ForbiddenException.class.getName().hashCode();

    public ForbiddenException() {
        super(CODE, "Forbidden");
    }
}
