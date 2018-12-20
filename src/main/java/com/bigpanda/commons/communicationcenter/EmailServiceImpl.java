package com.bigpanda.commons.communicationcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EmailServiceImpl implements EmailService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Vertx vertx;

    @Override
    public void sendMail(String eventName, String userId, Map<String, String> additionalParams, Handler<AsyncResult<Void>> resultHandler) {
        try {
           ObjectMapper mapper = new ObjectMapper();

            vertx.eventBus().send("kafkaSender", mapper.writeValueAsString(getShortEventDto(eventName, userId, additionalParams)), new DeliveryOptions().addHeader("topic", EmailService.SEND_MAIL_TOPIC));
       }
        catch (Exception e) {
           resultHandler.handle(Future.failedFuture(e));
            logger.error("An error", e);
        }

       resultHandler.handle(Future.succeededFuture());
    }

    private ShortEventDto getShortEventDto(String eventName, String userId, Map<String, String> additionalParams) {
        ShortEventDto shortEventDto = new ShortEventDto();

        shortEventDto.setEventName(eventName);
        shortEventDto.setUserId(userId);
        shortEventDto.setAdditionalParameters(additionalParams);

        return shortEventDto;
    }

    @Override
    public void close() {
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }
}
