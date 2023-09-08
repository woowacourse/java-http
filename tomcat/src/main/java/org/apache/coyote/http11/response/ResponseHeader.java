package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> headers;

    private ResponseHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeader createEmpty() {
        return new ResponseHeader(new LinkedHashMap<>());
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final var headerEntrySet : headers.entrySet()) {
            stringBuilder.append(headerEntrySet.getKey()).append(": ").append(headerEntrySet.getValue());
            stringBuilder.append("\r\n");
        }
        return stringBuilder.toString().trim();
    }
}
