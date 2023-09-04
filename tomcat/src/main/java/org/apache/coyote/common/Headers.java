package org.apache.coyote.common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> mapping = new HashMap<>();

    public Headers(final Map<String, String> headers) {
        mapping.putAll(headers);
    }

    public String getHeaderValue(final String name) {
        return mapping.getOrDefault(name, null);
    }

    public void addHeader(final String headerName, final String value) {
        mapping.put(headerName, value);
    }

    public List<String> headerNames() {
        return mapping.keySet()
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        final String mappingResult = mapping.keySet()
                .stream()
                .map(headerName -> "        " + headerName + " : " + mapping.get(headerName))
                .collect(Collectors.joining("," + System.lineSeparator()));

        return "Headers{" + System.lineSeparator() +
               mappingResult +
               '}';
    }
}
