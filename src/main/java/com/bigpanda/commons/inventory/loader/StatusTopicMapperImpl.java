package com.bigpanda.commons.inventory.loader;

import org.json.JSONObject;

import java.util.Map;

/**
 * 
 * Mapping diamond status to topic
 * @author erik
 *
 */
public class StatusTopicMapperImpl implements TopicProvider {
	
	private Map<String, String> statusTopicMapping;
	private String topicIfNotFound;
	
	public void setStatusTopicMapping(Map<String, String> statusTopicMapping) {
		this.statusTopicMapping = statusTopicMapping;
	}
	
	public void setTopicIfNotFound(String topicIfNotFound) {
		this.topicIfNotFound = topicIfNotFound;
	}

	@Override
	public String getTopic(JSONObject object) {
		String status = object.optString("status", null);
		if (status == null) {
			return topicIfNotFound;
		}
		
		return statusTopicMapping.get(status);
	}

}
