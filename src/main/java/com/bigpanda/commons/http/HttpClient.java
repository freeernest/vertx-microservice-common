package com.bigpanda.commons.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface HttpClient {
    <S> void send(String methodURI, HttpMethod method, JsonObject body, Class<S> responseClass, MultiMap headers, MultiMap queryParams, Handler<AsyncResult<S>> resultHandler, String... pathParams);
    <S> void send(String methodURI, HttpMethod method, JsonObject body, Class<S> responseClass, MultiMap headers, Handler<AsyncResult<S>> resultHandler, String... pathParams);
    <S> void send(String methodURI, HttpMethod method, JsonObject body, Class<S> responseClass, Handler<AsyncResult<S>> resultHandler, String... pathParams);
    <S> void send(String methodURI, HttpMethod method, JsonArray body, Class<S> responseClass, MultiMap headers, MultiMap queryParams, Handler<AsyncResult<S>> resultHandler, String... pathParams);
    <S> void send(String methodURI, HttpMethod method, JsonArray body, Class<S> responseClass, MultiMap headers, Handler<AsyncResult<S>> resultHandler, String... pathParams);
    <S> void send(String methodURI, HttpMethod method, JsonArray body, Class<S> responseClass, Handler<AsyncResult<S>> resultHandler, String... pathParams);
}
