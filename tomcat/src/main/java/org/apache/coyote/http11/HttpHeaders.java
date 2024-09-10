package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<HeaderType, String> fields;

    public HttpHeaders() {
        this.fields = new HashMap<>();
    }

    public void add(String headerLine) {
        String[] parts = headerLine.split(": ", 2);
        add(HeaderType.findByName(parts[0]), parts[1]);
    }

    public void add(HeaderType headerType, String value) {
        fields.put(headerType, value);
    }

    public Optional<String> findByName(HeaderType headerType) {
        return fields.entrySet().stream()
                .filter(entry -> entry.getKey().equals(headerType))
                .map(Entry::getValue)
                .findAny();
    }

    public Optional<String> findCookieByName(String name) {
        Optional<String> rawCookies = findByName(HeaderType.COOKIE);
        if (rawCookies.isEmpty()) {
            return Optional.empty();
        }

        HttpCookies cookies = new HttpCookies(rawCookies.get());
        return cookies.findByName(name);
    }

    public int getContentLength() {
        return Integer.parseInt(findByName(HeaderType.CONTENT_LENGTH).orElse("0"));
    }

    public String build() {
        return fields.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));
    }

    @Override
    public String toString() {
        return build();
    }
}
