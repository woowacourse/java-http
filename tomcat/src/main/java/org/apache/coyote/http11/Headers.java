package org.apache.coyote.http11;

import java.util.Map;
import java.util.TreeMap;

public class Headers {

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void add(final String header) {
        final int separator = header.indexOf(':');
        if (separator <= 0) {
            throw new IllegalArgumentException("잘못된 헤더: " + header);
        }

        final String name = header.substring(0, separator).trim();
        final String value = header.substring(separator + 1).trim();
        add(name, value);
    }

    public void add(final String name, final String value) {
        headers.put(name, value);
    }

    public Map<String, String> entries() {
        return Map.copyOf(headers);
    }

    public boolean contains(final String name) {
        return headers.containsKey(name);
    }

    public int contentLength() {
        final String length = headers.get("Content-Length");
        if (length == null) {
            return 0;
        }
        return Integer.parseInt(length);
    }

    public String cookie() {
        return headers.getOrDefault("Cookie", "");
    }

}
