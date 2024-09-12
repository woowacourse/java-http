package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Properties {
	private final Map<String, String> properties;

	public Properties() {
		this.properties = new HashMap<>();
	}

	public void add(String rawProperty) {
		String key = rawProperty.split("=")[0];
		String value = rawProperty.split("=")[1];
		this.properties.put(key, value);
	}

	public String get(String key) {
		return properties.get(key);
	}
}
