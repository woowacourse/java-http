package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.List;

public class Headers {

    private final LinkedHashMap<String, String> values;

    public Headers() {
        this(new LinkedHashMap<>());
    }

    private Headers(final LinkedHashMap<String, String> values) {
        this.values = values;
    }

    public static Headers of(final List<String> inputs) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        for (String header : inputs.subList(1, inputs.size())) {
            String[] splitHeader = header.split(": ", 2);
            headers.put(splitHeader[0], splitHeader[1]);
        }
        return new Headers(headers);
    }

    public void add(final String key, final String value) {
        this.values.put(key, value);
    }

    public String get(final String key) {
        return values.get(key);
    }

    public boolean contains(final String key) {
        return values.containsKey(key);
    }

    public LinkedHashMap<String, String> getValues() {
        return new LinkedHashMap<>(values);
    }
}
