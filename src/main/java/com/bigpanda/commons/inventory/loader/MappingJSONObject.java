package com.bigpanda.commons.inventory.loader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MappingJSONObject extends JSONObject {
	
	private Map<String, String> mappings;
	
	MappingJSONObject(Map<String, String> mappings) {
		this.mappings = mappings;
	}
	
	@Override
	public JSONObject put(String name, boolean value) throws JSONException {
		String mappedKey = mappings.get(name);
		if (mappedKey != null) {
			return super.put(mappedKey, value);
		}
		return this;
	}
	
	@Override
	public JSONObject put(String name, double value) throws JSONException {
		String mappedKey = mappings.get(name);
		if (mappedKey != null) {
			return super.put(mappedKey, value);
		}
		return this;
	}
	
	@Override
	public JSONObject put(String name, int value) throws JSONException {
		String mappedKey = mappings.get(name);
		if (mappedKey != null) {
			return super.put(mappedKey, value);
		}
		return this;
	}
	
	@Override
	public JSONObject put(String name, long value) throws JSONException {
		String mappedKey = mappings.get(name);
		if (mappedKey != null) {
			return super.put(mappedKey, value);
		}
		return this;
	}
	
	@Override
	public JSONObject put(String name, Object value) throws JSONException {
		String mappedKey = mappings.get(name);
		if (mappedKey != null) {
			return super.put(mappedKey, value);
		}
		return this;
	}
	
	@Override
	public JSONObject putOpt(String name, Object value) throws JSONException {
		String mappedKey = mappings.get(name);
		if (mappedKey != null) {
			return super.put(mappedKey, value);
		}
		return this;
	}

}
