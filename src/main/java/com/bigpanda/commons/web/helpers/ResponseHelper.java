package com.bigpanda.commons.web.helpers;

import com.bigpanda.commons.security.ForbiddenException;
import com.bigpanda.commons.security.UnauthorizedException;
import com.bigpanda.commons.web.errors.ErrorCode;
import com.bigpanda.commons.web.errors.GeneralErrorCode;
import com.bigpanda.commons.web.exceptions.BadRequestException;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by erik on 7/24/17.
 */
public class ResponseHelper {

    public static void sendOkay(HttpServerResponse response, Object data, int statusCode) {
        String res;

        if (data instanceof JsonObject) {
            res = ((JsonObject) data).encodePrettily();
        } else if (data instanceof JsonArray) {
            res = ((JsonArray) data).encodePrettily();
        } else if (data instanceof List) {
            res = new JsonArray((List) data).encodePrettily();
        } else {
            res = data == null ? null : data.toString();
        }
        response.putHeader("content-type", "application/json").setStatusCode(statusCode).end(res == null ? "" : res);
    }

    public static void sendError(HttpServerResponse response, int statusCode, String error, ErrorCode errorCode) {
        sendError(response, statusCode, error, errorCode, null);
    }

    public static void sendError(HttpServerResponse response, int statusCode, String error, ErrorCode errorCode, JsonObject debugInfo) {
        JsonObject returnJson = new JsonObject();
        if (errorCode != null) {
            returnJson.put("code", errorCode.getCode());
        }
        returnJson.put("message", error);
        if (debugInfo != null && debugInfo.containsKey("data")) {
            returnJson.put("data", debugInfo.getValue("data"));
        }
        response.putHeader("content-type", "application/json")
                .setStatusCode(statusCode)
                .end(returnJson.encodePrettily());
    }

    public static void sendError(HttpServerResponse response, Throwable throwable) {
        if (throwable instanceof ReplyException) {

            int failureCode = ((ReplyException) throwable).failureCode();
            if (failureCode == -1) {
                sendError(response, 500, "Exception on server", GeneralErrorCode.GENERAL_ERROR);
            } else {
                JsonObject debugInfo = null;
                if (throwable instanceof ServiceException) {
                    debugInfo = ((ServiceException) throwable).getDebugInfo();
                }
                sendError(response, resolveHttpStatusCode((ReplyException) throwable), throwable.getMessage(), () -> failureCode, debugInfo);
            }
        } else {

            sendError(response, 500, throwable.getMessage(), GeneralErrorCode.GENERAL_ERROR);
        }
    }

    private static int resolveHttpStatusCode(ReplyException e) {
        Integer httpStatus = httpStatusMapper.get(e.failureCode());

        return httpStatus == null ? 400 : httpStatus;
    }


    private static Map<Integer, Integer> httpStatusMapper = new HashMap<>();

    static {
        httpStatusMapper.put(UnauthorizedException.CODE, 401);
        httpStatusMapper.put(ForbiddenException.CODE, 403);
        httpStatusMapper.put(BadRequestException.CODE, 400);
    }

}
