package org.apache.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class Headers {

    private final Map<HttpHeader, String> mapping = new LinkedHashMap<>();

    public Headers() {
        mapping.put(HttpHeader.CONTENT_TYPE, HttpMime.DEFAULT.getValue());
        mapping.put(HttpHeader.CONTENT_LENGTH, String.valueOf(0));
    }

    public void put(final HttpHeader key, final String value) {
        mapping.put(key, value);
    }

    public String parse() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<HttpHeader, String> entry : mapping.entrySet()) {
            builder.append(entry.getKey().getValue())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" \r\n");
        }
        return builder.toString();
    }
}
