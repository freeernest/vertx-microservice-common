package com.bigpanda.commons.verticles;

import com.bigpanda.commons.http.HttpHandler;
import com.bigpanda.commons.json.JsonConversion;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erik on 2/8/18.
 */
public class EventBusExporterVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<String> addresses;
    private List<MessageConsumer> messageConsumers;
    private HttpClient httpClient;

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    @Override
    public void start() throws Exception {

        httpClient = vertx.createHttpClient();
        messageConsumers = new ArrayList<>(addresses.size());

        addresses.forEach(address -> {
            messageConsumers.add(vertx.eventBus().consumer(address, this::handler));
        });

    }

    private void handler(Message<JsonObject> message) {
        httpClient
                .postAbs("http://localhost:8080/events", HttpHandler.responseHandler(event -> {
                    if (event.failed()) {
                        logger.error("An error", event.cause());
                        message.fail(1, event.cause().getMessage());
                    } else {
                        message.reply(new JsonObject(event.result()).getValue("result"));
                    }
                }))
                .exceptionHandler(event -> {
                    logger.error("An error", event);
                    message.fail(1, event.getMessage());
                })
//                .putHeader("bla", "bla") // implement authentication somehow :)
                .putHeader("destAddr", message.address())
                .putHeader("headers", JsonConversion.fromMultiMap(message.headers()).toString())
                .end(message.body().toBuffer());
    }

    @Override
    public void stop() throws Exception {
        httpClient.close();
        messageConsumers.forEach(messageConsumer -> messageConsumer.unregister());
    }
}
