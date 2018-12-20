package com.bigpanda.commons.http;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.impl.BodyCodecImpl;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HttpClientImpl implements HttpClient {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private WebClient webClient;
    private Integer port;
    private String host;
    private String apiURI;

    public HttpClientImpl(Vertx vertx, String host, Integer port, String apiURI) {
        this.webClient = WebClient.create(vertx);
        this.port = port;
        this.host = host;
        this.apiURI = apiURI;
    }

    @Override
    public <S> void send(String methodURI, HttpMethod method, JsonObject body, Class<S> responseClass, MultiMap headers, MultiMap queryParams, Handler<AsyncResult<S>> resultHandler, String... pathParams) {
        try {
            String uri = apiURI + constructURI(methodURI, pathParams);

            HttpRequest<Buffer> request = webClient.request(method, port, host, uri);

            Handler<AsyncResult<HttpResponse<Buffer>>> asyncResultHandler = getAsyncResultHandler(resultHandler, responseClass);

            if (headers != null) {
                request.headers().addAll(headers);
            }

            if (queryParams != null) {
                request.queryParams().addAll(queryParams);
            }

            if (body != null && isBodyHttpMethod(method)) {
                request.sendJsonObject(body, asyncResultHandler);
            } else {
                request.send(asyncResultHandler);
            }
        }
        catch(Exception e) {
            resultHandler.handle(ServiceException.fail(500, e.getMessage()));
        }
    }

    @Override
    public <S> void send(String methodURI, HttpMethod method, JsonObject body, Class<S> responseClass, MultiMap headers, Handler<AsyncResult<S>> resultHandler, String... pathParams) {
        send(methodURI, method, body, responseClass, headers, null, resultHandler, pathParams);
    }

    @Override
    public <S> void send(String methodURI, HttpMethod method, JsonObject body, Class<S> responseClass, Handler<AsyncResult<S>> resultHandler, String... pathParams) {
        send(methodURI, method, body, responseClass, null, resultHandler, pathParams);
    }

    @Override
    public <S> void send(String methodURI, HttpMethod method, JsonArray body, Class<S> responseClass, MultiMap headers, MultiMap queryParams, Handler<AsyncResult<S>> resultHandler, String... pathParams) {
        try {
            String constructedURI = constructURI(methodURI, pathParams);

            HttpRequest<Buffer> request = webClient.request(method, port, host, constructedURI);

            Handler<AsyncResult<HttpResponse<Buffer>>> asyncResultHandler = getAsyncResultHandler(resultHandler, responseClass);

            if (headers != null) {
                request.headers().addAll(headers);
            }

            if (queryParams != null) {
                request.queryParams().addAll(queryParams);
            }

            if (isBodyHttpMethod(method) && body != null) {
                request.sendBuffer(body.toBuffer(), asyncResultHandler);
            } else {
                request.send(asyncResultHandler);
            }
        }
        catch(Exception e) {
            resultHandler.handle(ServiceException.fail(500, e.getMessage()));
        }
    }

    @Override
    public <S> void send(String methodURI, HttpMethod method, JsonArray body, Class<S> responseClass, MultiMap headers, Handler<AsyncResult<S>> resultHandler, String... pathParams) {
        send(methodURI, method, body, responseClass, headers, null, resultHandler, pathParams);
    }

    @Override
    public <S> void send(String methodURI, HttpMethod method, JsonArray body, Class<S> responseClass, Handler<AsyncResult<S>> resultHandler, String... pathParams) {
        send(methodURI, method, body, responseClass,null, resultHandler, pathParams);
    }

    private String constructURI(String methodURI, String[] pathParams) throws Exception {
        List<String> uriParams = Arrays.asList(methodURI.split("/"));

        uriParams = uriParams.stream()
                .filter(currURIComponent -> currURIComponent.startsWith(":"))
                .collect(Collectors.toList());

        if(!uriParams.isEmpty()) {
            if(pathParams != null && pathParams.length == uriParams.size()) {
                int index = 0;

                for (String currURIComponent : uriParams) {
                    methodURI = methodURI.replace(currURIComponent, pathParams[index]);

                    index++;
                }
            }
            else {
                throw new Exception("path parameters doesn't match uri pattern");
            }
        }

        return methodURI;
    }

    private <S> Handler<AsyncResult<HttpResponse<Buffer>>> getAsyncResultHandler(Handler<AsyncResult<S>> resultHandler, Class<S> responseClass) {
        return ar -> {
            if (ar.succeeded()) {
                if (ar.result().statusCode() >= 200 && ar.result().statusCode() < 300) {
                    if (responseClass.equals(Void.class)) {
                        resultHandler.handle(Future.succeededFuture());
                    }
                    else {
                        Function decoderFunction = getResponseDecoderByClass(responseClass);

                        S res = (S)decoderFunction.apply(ar.result().body());

                        resultHandler.handle(Future.succeededFuture(res));
                    }
                } else {
                    JsonObject response = ar.result().body().toJsonObject();

                    String message = response.getString("message");
                    Integer code = response.getInteger("code");

                    logger.error("An error, Code: " + code + " Msg:" + message);
                    resultHandler.handle(ServiceException.fail(code, message));
                }
            }
            else {
                logger.error("An error", ar.cause());
                resultHandler.handle(ServiceException.fail(500, ar.cause().getMessage()));
            }
        };
    }

    private <S> Function getResponseDecoderByClass(Class<S> responseClass) {
        Function conversionFunc;

        if (responseClass.equals(JsonObject.class)) {
            conversionFunc = BodyCodecImpl.JSON_OBJECT_DECODER;
        }
        else if (responseClass.equals(JsonArray.class)) {
            conversionFunc = BodyCodecImpl.JSON_ARRAY_DECODER;
        }
        else if (responseClass.equals(String.class)) {
            conversionFunc = BodyCodecImpl.UTF8_DECODER;
        }
        else {
            conversionFunc = BodyCodecImpl.jsonDecoder(responseClass);
        }

        return conversionFunc;
    }

    private boolean isBodyHttpMethod(HttpMethod method) {
        return method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE);
    }
}
