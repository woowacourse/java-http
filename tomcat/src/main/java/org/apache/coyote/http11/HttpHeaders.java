package org.apache.coyote.http11;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record HttpHeaders(Map<String, String> values) {

    public HttpHeaders(final Map<String, String> values) {
        final Map<String, String> headers = new LinkedHashMap<>();
        values.forEach((name, value) -> {
            headers.keySet().removeIf(key -> key.equalsIgnoreCase(name));
            headers.put(name, value);
        });
        this.values = Collections.unmodifiableMap(headers);
    }

    public int contentLength() {
        return Integer.parseInt(getOrDefault("content-length", "0"));
    }

    public boolean isFormUrlEncoded() {
        final String mediaType = getOrDefault("content-type", "").split(";", 2)[0].strip();
        return "application/x-www-form-urlencoded".equalsIgnoreCase(mediaType);
    }

    public String get(final String name) {
        for (final var header : values.entrySet()) {
            if (header.getKey().equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public String getOrDefault(final String name, final String defaultValue) {
        final String value = get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public HttpHeaders with(final String name, final String value) {
        final Map<String, String> headers = new LinkedHashMap<>(values);
        headers.keySet().removeIf(key -> key.equalsIgnoreCase(name));
        headers.put(name, value);
        return new HttpHeaders(headers);
    }

    public Map<String, String> values() {
        return Map.copyOf(values);
    }
}
