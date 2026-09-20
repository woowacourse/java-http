package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class HttpHeaders {

    private final Map<String, List<String>> values = new LinkedHashMap<>();

    void add(final String name, final String value) {
        values.computeIfAbsent(Objects.requireNonNull(name), key -> new ArrayList<>())
                .add(Objects.requireNonNull(value));
    }

    String toHeaderLines() {
        return values.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> entry.getKey() + ": " + value + " "))
                .collect(Collectors.joining("\r\n"));
    }
}
