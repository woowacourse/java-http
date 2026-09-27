package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HttpHeaders {

    private final Map<String, List<String>> values = new LinkedHashMap<>();

    public static HttpHeaders from(final List<String> headerLines) {
        final HttpHeaders headers = new HttpHeaders();
        for (String headerLine : headerLines) {
            final int separatorIndex = headerLine.indexOf(':');
            if (separatorIndex <= 0) {
                continue;
            }

            final String name = headerLine.substring(0, separatorIndex).trim();
            final String value = headerLine.substring(separatorIndex + 1).trim();
            if (!name.isEmpty()) {
                headers.add(name, value);
            }
        }
        return headers;
    }

    public void add(final String name, final String value) {
        values.computeIfAbsent(Objects.requireNonNull(name), key -> new ArrayList<>())
                .add(Objects.requireNonNull(value));
    }

    public void set(final String name, final String value) {
        final String headerName = values.keySet().stream()
                .filter(key -> key.equalsIgnoreCase(name))
                .findFirst()
                .orElse(Objects.requireNonNull(name));
        values.put(headerName, new ArrayList<>(List.of(Objects.requireNonNull(value))));
    }

    public Optional<String> get(final String name) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .flatMap(entry -> entry.getValue().stream())
                .findAny();
    }

    public String toHeaderLines() {
        return values.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> entry.getKey() + ": " + value + " "))
                .collect(Collectors.joining("\r\n"));
    }
}
