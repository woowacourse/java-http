package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpCookies {

    private final Map<String, String> fields;

    public HttpCookies() {
        this.fields = new HashMap<>();
    }

    public HttpCookies(String value) {
        this.fields = Arrays.stream(value.split("; "))
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(part -> part[0], part -> part[1]));
    }

    public Optional<String> findByName(String name) {
        return fields.entrySet().stream()
                .filter(entry -> entry.getKey().equals(name))
                .map(Map.Entry::getValue)
                .findAny();
    }
}
