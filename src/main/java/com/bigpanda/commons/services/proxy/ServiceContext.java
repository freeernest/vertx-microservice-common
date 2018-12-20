package com.bigpanda.commons.services.proxy;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class ServiceContext {
    private JsonObject principal;
    private String authToken;
    private String btrxId;

    public ServiceContext() {
    }

    // Mandatory for data objects
    public ServiceContext(JsonObject jsonObject) {
        ServiceContextConverter.fromJson(jsonObject, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ServiceContextConverter.toJson(this, json);
        return json;
    }

    public JsonObject getPrincipal() {
        return principal;
    }

    public void setPrincipal(JsonObject principal) {
        this.principal = principal;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getBtrxId() {
        return btrxId;
    }

    public void setBtrxId(String btrxId) {
        this.btrxId = btrxId;
    }
}
