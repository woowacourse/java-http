package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

import java.util.HashMap;
import java.util.Map;

public class Properties {
	private static final int KEY_INDEX_OF_PROPERTY = 0;
	private static final int VALUE_INDEX_OF_PROPERTY = 1;

	private final Map<String, String> properties;

	public Properties() {
		this.properties = new HashMap<>();
	}

	public void add(String rawProperty) {
		String key = rawProperty.split(BODY_KEY_VALUE_DELIMITER.getValue())[KEY_INDEX_OF_PROPERTY];
		String value = rawProperty.split(BODY_KEY_VALUE_DELIMITER.getValue())[VALUE_INDEX_OF_PROPERTY];
		this.properties.put(key, value);
	}

	public String get(String key) {
		return properties.get(key);
	}
}
