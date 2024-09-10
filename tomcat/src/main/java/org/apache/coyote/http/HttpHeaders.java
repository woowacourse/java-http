package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpHeaders {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final String SEPARATOR = ": ";

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public HttpHeaders() {
        headers = new ConcurrentHashMap<>();
    }

    public HttpHeaders(final BufferedReader reader) throws IOException {
        headers = new ConcurrentHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            put(line);
        }
    }

    public String get(final String name) {
        if (headers.containsKey(name)) {
            return headers.get(name);
        }
        return null;
    }

    private void put(final String headerLine) {
        if (!headerLine.contains(SEPARATOR)) {
            throw new IllegalArgumentException(headerLine);
        }
        String[] split = headerLine.split(SEPARATOR);
        String name = split[NAME_INDEX];
        String value = split[VALUE_INDEX];
        put(name, value);
    }

    public void put(final String name, final String value) {
        headers.put(name, value);
    }

    public String asString() {
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            result.append(key)
                    .append(": ")
                    .append(value)
                    .append(" \r\n");
        }
        return result.toString();
    }
}
