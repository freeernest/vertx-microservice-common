package com.bigpanda.commons.web.exceptions;

import io.vertx.serviceproxy.ServiceException;

/**
 * Created by erik on 10/25/17.
 */
public class BadRequestException extends ServiceException {

    public static int CODE = BadRequestException.class.getName().hashCode();

    public BadRequestException() {
        super(CODE, "Bad request");
    }
}
