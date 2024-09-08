package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {

    private final Map<String, List<String>> values;

    public HttpResponseHeader() {
        this.values = new LinkedHashMap<>();
    }

    public void add(String name, String value) {
        this.values.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(";", entry.getValue()) + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
