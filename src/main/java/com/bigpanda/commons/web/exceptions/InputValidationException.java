package com.bigpanda.commons.web.exceptions;

import com.bigpanda.commons.validations.InputValidator;
import com.bigpanda.commons.web.errors.GeneralErrorCode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;

/**
 * Created by erik on 10/25/17.
 */
public class InputValidationException extends ServiceException {

    public InputValidationException(JsonArray errors) {
        super(GeneralErrorCode.INVALID_INPUT.getCode(), "Invalid input field", new JsonObject().put("data", errors));
    }

    public InputValidationException(String field, String error) {
        this(InputValidator.addError(field, error));
    }
}
