package com.bigpanda.commons.web.helpers;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

/**
 * Created by erik on 9/24/17.
 */
public class RequestHelper {

    /**
     * Return the first param value with the specified name
     * @param request
     * @param paramName
     * @param def default value, In case the parameter is not present
     * @return parameter value
     */
    public static String getParam(HttpServerRequest request, String paramName, String def) {
        String paramValue = request.getParam(paramName);
        if (paramValue != null) {
            return paramValue;
        }
        return def;
    }

    /**
     * Return the first param value with the specified name
     * @param context
     * @param paramName
     * @param def default value, In case the parameter is not present
     * @return parameter value
     */
    public static String getFormParam(RoutingContext context, String paramName, String def) {
        final String body = context.getBodyAsString();
        QueryStringDecoder qsd = new QueryStringDecoder(body, false);
        final List<String> pList = qsd.parameters().get(paramName);
        return pList == null || pList.size() == 0 ? def : pList.get(0);
    }
}
