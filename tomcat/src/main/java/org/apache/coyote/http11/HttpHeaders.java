package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String EMPTY_VALUE = "";
    private static final String CONTENT_TYPE_DELIMITER = ",";

    private final Map<String, String> values = new HashMap<>();

    public void add(final String key, final String value) {
        values.put(key, value);
    }

    public String get(final String headerName) {
        return values.getOrDefault(headerName, EMPTY_VALUE);
    }

    public String getContentType() {
        String value = values.getOrDefault("Content-Type", EMPTY_VALUE);
        if (value.isEmpty()) {
            value = values.get("Accept");
        }
        return value.split(CONTENT_TYPE_DELIMITER)[0];
    }

    public Map<String, String> getValues() {
        return values;
    }
}
