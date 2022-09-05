package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {

    private final Map<String, Object> values;

    public HttpHeaders() {
        values = new HashMap<>();
    }

    public void addValue(final String key, final Object value) {
        values.put(key, value);
    }

    public String generate(final String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, Object> entry : values.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" ")
                    .append(separator);
        }
        return stringBuilder.toString();
    }
}
