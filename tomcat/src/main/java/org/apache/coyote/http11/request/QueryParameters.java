package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QueryParameters {

    private final Map<String, List<String>> values;

    public QueryParameters(Map<String, List<String>> values) {
        Map<String, List<String>> copied = new HashMap<>();
        values.forEach((name, parameters) -> copied.put(name, List.copyOf(parameters)));
        this.values = Map.copyOf(copied);
    }

    public Optional<String> first(String name) {
        return all(name).stream().findFirst();
    }

    public List<String> all(String name) {
        return values.getOrDefault(name, List.of());
    }
}
