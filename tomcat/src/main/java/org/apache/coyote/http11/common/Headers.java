package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> values;

    public Headers() {
        values = new HashMap<>();
    }

    public void addLocation(String location) {
        values.put("Location", location);
    }

    public void add(String name, String value) {
        values.put(name, value);
    }

    public String getLocation() {
        return values.get("Location");
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(" \r\n"));
    }
}
