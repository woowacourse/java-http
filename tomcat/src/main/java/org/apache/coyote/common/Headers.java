package org.apache.coyote.common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> mapping;

    private Headers(final Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public static Headers empty() {
        return new Headers(new HashMap<>());
    }

    public void addHeader(final String headerName, final String headerValue) {
        mapping.put(headerName, headerValue);
    }

    public String getHeaderValue(final String headerName) {
        return mapping.getOrDefault(headerName, null);
    }

    public List<String> headerNames() {
        return mapping.keySet()
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Headers headers = (Headers) o;
        return Objects.equals(mapping, headers.mapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapping);
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
