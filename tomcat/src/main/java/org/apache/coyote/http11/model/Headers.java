package org.apache.coyote.http11.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers;

    private Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers from(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            int headerDelimiterIndex = line.indexOf(":");

            if (headerDelimiterIndex != -1) {
                String key = line.substring(0, headerDelimiterIndex).trim();
                String value = line.substring(headerDelimiterIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return new Headers(headers);
    }

    public String getHeaderValue(final String key) {
        return headers.get(key);
    }

    public boolean containsKey(final String key) {
        return headers.containsKey(key);
    }
}
