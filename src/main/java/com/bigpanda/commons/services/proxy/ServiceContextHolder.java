package com.bigpanda.commons.services.proxy;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class ServiceContextHolder extends ThreadLocal<ServiceContext> {
    private static ServiceContextHolder instance = new ServiceContextHolder();

    /**
     * Used to initialize new context from authToken
     * @param authToken
     */
    public static void createNewContext(String authToken) {
        ServiceContext context = new ServiceContext();
        context.setBtrxId(UUID.randomUUID().toString());
        context.setAuthToken(authToken);
        instance.set(context);
    }

    /**
     * Used to initialize context from Json, Usually used to decode it from wire and put in ThreadLocal
     * @param context
     */
    public static void create(String context) {
        instance.set(new ServiceContext(new JsonObject(context)));
    }

    /**
     * Used to set an existing {@link ServiceContext}
     * @param serviceContext
     */
    public static void create(ServiceContext serviceContext) {
        instance.set(serviceContext);
    }

    /**
     *
     */
    public static void clean() {
        instance.remove();
    }

    /**
     * Get current {@link ServiceContext}
     * @return ServiceContext
     */
    public static ServiceContext getContext() {
        return instance.get();
    }

    public static String getUserId() {
        if (getContext() != null && getContext().getPrincipal() != null) {
            return getContext().getPrincipal().getString("sub");
        }

        return null;
    }
}
