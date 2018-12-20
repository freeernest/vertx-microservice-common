package com.bigpanda.commons.verticles;

import com.bigpanda.commons.communicationcenter.EmailService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.serviceproxy.ServiceBinder;

public class EmailServiceVerticle extends AbstractVerticle {
    private MessageConsumer consumer;
    private EmailService emailService;

    @Override
    public void start() throws Exception {
        ServiceBinder serviceBinder = new ServiceBinder(vertx)
                .setAddress(EmailService.SERVICE_ADDRESS);

        consumer = serviceBinder.register(EmailService.class, emailService);
    }

    @Override
    public void stop() throws Exception {
        emailService.close();
        consumer.unregister();
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
