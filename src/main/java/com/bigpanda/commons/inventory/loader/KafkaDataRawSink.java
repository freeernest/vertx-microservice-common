package com.bigpanda.commons.inventory.loader;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class KafkaDataRawSink implements DataRawSink {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Producer<String, String> producer;
	private Map<String, String> mappings;
	private Properties properties;
	private TopicProvider topicProvider;
	
	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setTopicProvider(TopicProvider topicProvider) {
		this.topicProvider = topicProvider;
	}
	
	public void init() {
		producer = new KafkaProducer<>(properties);
	}
	
	@Override
	public void pour(JSONObject object) {
		String topic = topicProvider.getTopic(object);
		producer.send(new ProducerRecord<String, String>(topic, null, object.toString()));
	}
	
	@Override
	public MappingJSONObject createMappingJSONObject() {
		return new MappingJSONObject(mappings);
	}
	
	public void close() {
		producer.close();
	}

}
