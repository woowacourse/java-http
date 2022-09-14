package org.apache.coyote;

import java.util.LinkedHashMap;
import java.util.Map;

public class Headers {

    private static final String DEFAULT_CONTENT_LENGTH = "0";

    private final Map<HttpHeader, String> values = new LinkedHashMap<>();

    public Headers() {
        values.put(HttpHeader.CONTENT_TYPE, HttpMime.DEFAULT.getValue());
        values.put(HttpHeader.CONTENT_LENGTH, DEFAULT_CONTENT_LENGTH);
    }

    public void put(final HttpHeader key, final String value) {
        this.values.put(key, value);
    }

    public String parse() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<HttpHeader, String> entry : values.entrySet()) {
            builder.append(String.format("%s: %s \r%n", entry.getKey().getValue(), entry.getValue()));
        }
        return builder.toString();
    }

    public String find(final HttpHeader httpHeader) {
        return values.get(httpHeader);
    }
}
