package org.apache.coyote.http11;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Headers {

    private final Map<String, String> values;
    private final Map<String, String> names;

    public Headers() {
        this.values = new LinkedHashMap<>();
        this.names = new LinkedHashMap<>();
    }

    public void put(final String name, final String value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("헤더 이름은 비어 있을 수 없습니다.");
        }
        final String normalizedName = normalize(name);
        names.putIfAbsent(normalizedName, name.trim());
        values.put(normalizedName, value == null ? "" : value.trim());
    }

    public String get(final String name) {
        if (name == null) {
            return null;
        }
        return values.get(normalize(name));
    }

    public boolean contains(final String name) {
        return get(name) != null;
    }

    public int size() {
        return values.size();
    }

    public Map<String, String> asMap() {
        final Map<String, String> result = new LinkedHashMap<>();
        values.forEach((name, value) -> result.put(names.get(name), value));
        return Collections.unmodifiableMap(result);
    }

    private String normalize(final String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
