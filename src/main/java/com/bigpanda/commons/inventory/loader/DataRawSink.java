package com.bigpanda.commons.inventory.loader;

import org.json.JSONObject;

public interface DataRawSink {

	
	/**
	 * Sends the object to the messaging layer
	 * @param object - JSONObject
	 */
	void pour(JSONObject object);

	/**
	 * Generates JSONObject that handles all configured mappings 
	 * @return MappingJSONObject 
	 */
	MappingJSONObject createMappingJSONObject();

}
