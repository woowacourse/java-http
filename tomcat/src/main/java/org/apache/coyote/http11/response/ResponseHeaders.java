package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public String convertResponseHeadersMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((name, value) ->
                stringBuilder
                        .append(name)
                        .append(": ")
                        .append(value).append("\r\n"));
        return stringBuilder.toString();
    }
}
