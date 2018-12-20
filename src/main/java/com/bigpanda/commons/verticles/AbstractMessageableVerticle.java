package com.bigpanda.commons.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by erik on 7/10/17.
 */
public abstract class AbstractMessageableVerticle<T> extends AbstractVerticle {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String BP_ADDRESS = "bpAddress";
    public static final String REPLY_RESULT = "reply-result";

    private String inAddress;
    private String outAddress;
    private boolean publishMessage = false;
    private MessageConsumer<T> consumer;
    private boolean localConsumer = false;

    public void setInAddress(String inAddress) {
        this.inAddress = inAddress;
    }

    public void setOutAddress(String outAddress) {
        this.outAddress = outAddress;
    }

    protected String getOutAddress() {
        return outAddress;
    }

    public void setPublishMessage(boolean publishMessage) {
        this.publishMessage = publishMessage;
    }

    public void setLocalConsumer(boolean localConsumer) {
        this.localConsumer = localConsumer;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final EventBus eventBus = vertx.eventBus();
        if (inAddress != null) {
            if (localConsumer) {
                consumer = eventBus.localConsumer(inAddress, getMessageHandler(eventBus));
            } else {
                consumer = eventBus.consumer(inAddress, getMessageHandler(eventBus));
            }
            consumer.completionHandler(res -> {
                if (res.succeeded()) {
                    ready(startFuture);
                } else {
                    logger.error("Registration failed!", res.cause());
                }
            });
        } else {
            ready(startFuture);
        }
    }

    private Handler<Message<T>> getMessageHandler(EventBus eventBus) {
        return message -> {
            T outMessage = null;
            MultiMap headers = message.headers();
            try {
                outMessage = processMessage(message);

            } catch (Exception e) {
                logger.error("failed to process message", e);
                message.fail(1, e.getMessage());
            }

            if (outMessage != null) {
                if (headers.contains(REPLY_RESULT)) {
                    message.reply(outMessage);
                } else {
                    DeliveryOptions deliveryOptions = getDeliveryOptions(headers);

                    if (publishMessage) {
                        eventBus.publish(outAddress, outMessage, deliveryOptions);
                    } else {
                        eventBus.send(outAddress, outMessage, deliveryOptions);
                    }
                }
            }
        };
    }

    protected DeliveryOptions getDeliveryOptions(MultiMap headers) {
        String bpAddress = headers.get(BP_ADDRESS);
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        if (bpAddress != null)
            deliveryOptions.addHeader(BP_ADDRESS, bpAddress);
        return deliveryOptions;
    }

    public void ready(Future<Void> startFuture) {
        startFuture.complete();
    }

    @Override
    public void stop() throws Exception {
        if (consumer != null) {
            consumer.unregister();
        }
    }

    protected T processMessage(Message<T> message) {
        return processMessage(message.body(), message.headers());
    }

    protected T processMessage(T body, MultiMap headers) {
        return processMessage(body);
    }

    protected T processMessage(T message) {
        return null;
    }
}
