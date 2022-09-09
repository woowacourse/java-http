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
        values.forEach((key, value) ->
                builder.append(String.format("%s: %s \r%n", key.getValue(), value)));
        return builder.toString();
    }

    public String find(final HttpHeader httpHeader) {
        return values.get(httpHeader);
    }
}
