package com.bigpanda.commons.communicationcenter;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Map;

@VertxGen
@ProxyGen
public interface EmailService {
    String SERVICE_ADDRESS = "email.service";
    String SEND_MAIL_TOPIC = "emailsend-topic";

     void sendMail(String eventName, String userId, Map<String, String> additionalParams, Handler<AsyncResult<Void>> resultHandler);

    @ProxyClose
    void close();
}
