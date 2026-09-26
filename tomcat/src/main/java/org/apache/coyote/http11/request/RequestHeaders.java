package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class RequestHeaders {

    private final Map<String, List<String>> values;

    public RequestHeaders(Map<String, List<String>> values) {
        Map<String, List<String>> normalized = new HashMap<>();

        values.forEach((key, headerValues) -> {
            String normalizedName = key.toLowerCase(Locale.ROOT);
            List<String> normalizedValues = normalized.computeIfAbsent(
                    normalizedName,
                    ignored -> new ArrayList<>()
            );
            normalizedValues.addAll(headerValues);
        });
        normalized.replaceAll((name, headerValues) -> List.copyOf(headerValues));

        this.values = Map.copyOf(normalized);
    }

    public Optional<String> first(String name) {
        return values.getOrDefault(name.toLowerCase(Locale.ROOT), List.of())
                .stream()
                .findFirst();
    }

    public List<String> all(String name) {
        return values.getOrDefault(
                name.toLowerCase(Locale.ROOT),
                List.of()
        );
    }

    public boolean contains(String name) {
        return values.containsKey(
                name.toLowerCase(Locale.ROOT)
        );
    }
}
