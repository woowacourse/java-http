package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = Map.copyOf(headers);
    }

    public static RequestHeaders from(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] headerParts = line.split(":");
            if (headerParts.length == 2) {
                String key = headerParts[0].trim();
                String value = headerParts[1].trim();
                headers.put(key, value);
            }
        }
        return new RequestHeaders(headers);
    }

    public String getHeader(final String key) {
        return headers.get(key);
    }
}
