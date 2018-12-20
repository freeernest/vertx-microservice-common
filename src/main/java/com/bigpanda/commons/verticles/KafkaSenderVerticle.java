package com.bigpanda.commons.verticles;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by avnerlevinstien on 05/07/2017.
 */
public abstract class KafkaSenderVerticle<T> extends AbstractMessageableVerticle<T> {
    public static final String SEND_REPLY = "send_reply_header";
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private String destination;
    private String backPressureDropAddress;
    private KafkaProducer<String, String> producer;
    private Properties kafkaConfig;


	public void setDestination(String destination) {
        this.destination = destination;
    }

    public void  setBackPressureDropAddress(String backPressureDropAddress) {
        this.backPressureDropAddress = backPressureDropAddress;
    }

    public void setKafkaConfig(Properties kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
	    try {
            start();
        } catch (Exception e) {
	        logger.error(e.getMessage(), e);
        }
        super.start(startFuture);
    }

    @Override
    public void start() throws Exception {
        producer = KafkaProducer.create(vertx, kafkaConfig);
    }

    @Override
    public void stop() throws Exception {
    	producer.close(result -> {
    		if (result.succeeded()) {
    			logger.info("kafka producer closed");
			} else {
				logger.info("kafka producer closing failed");
			}
    	});
    }

    @Override
    protected T processMessage(Message<T> message) {
	    String topic = destination;
	    MultiMap headers = message.headers();
	    if (headers.contains("topic")) {
	        topic = headers.get("topic");
        }
        if (headers.contains(SEND_REPLY)) {
            producer.write(KafkaProducerRecord.create(topic, null, getMessage(message.body())), result -> {
                if (result.succeeded()) {
                    message.reply(null);
                } else {
                    message.fail(100, result.cause().getMessage());
                    logger.error("An Error", result.cause());
                }
            });
        } else {
            producer.write(KafkaProducerRecord.create(topic, null, getMessage(message.body())));
        }

        if (backPressureDropAddress != null)
            getVertx().eventBus().send(backPressureDropAddress, null);

        return null;
    }

    protected abstract String getMessage(T message);
}
