package org.apache.coyote.http11.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public class HttpHeaders {
    private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void add(String key, String value) {
        headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public Optional<String> getFirst(String key) {
        return headers.containsKey(key)
                ? Optional.of(headers.get(key).get(0))
                : Optional.empty();
    }

    public List<String> getAll(String key) {
        return headers.getOrDefault(key, List.of());
    }

    public Set<String> keySet() {
        return headers.keySet();
    }
}
