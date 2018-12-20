package com.bigpanda.commons.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by avnerlevinstien on 05/07/2017.
 */
public class KafkaListenerRouterVerticle extends AbstractVerticle {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> routings;
    private KafkaConsumer<String, String> kafkaConsumer;
    private Properties kafkaConfig;

    @Override
    public void start(Future<Void> startFuture) {

        final EventBus eventBus = vertx.eventBus();

        kafkaConsumer = KafkaConsumer.create(vertx, kafkaConfig);

        kafkaConsumer.handler(event -> {
            String destAddress = routings.get(event.topic());
            if (destAddress != null) {
                try {
                    eventBus.send(destAddress, new JsonObject(event.value()));
                } catch (Exception e) {
                    logger.error("An error", e);
                }
            }
        });

        kafkaConsumer.subscribe(routings.keySet(), startFuture);
    }

    @Override
    public void stop() {
    	kafkaConsumer.close(result -> {
    		if (result.succeeded()) {
    			logger.info("kafkaConsumer closed");
			} else {
				logger.info("kafkaConsumer closing failed");
			}
    	});
    }

    public void setRoutings(Map<String, String> routings) {
        this.routings = routings;
    }

    public void setKafkaConfig(Properties kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

}
