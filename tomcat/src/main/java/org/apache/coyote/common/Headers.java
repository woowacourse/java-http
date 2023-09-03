package org.apache.coyote.common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private static final String HEADER_DELIMITER = ":";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> mapping = new HashMap<>();

    public Headers(final List<String> headersWithValue) {
        headersWithValue.stream()
                .map(headerWithValue -> headerWithValue.split(HEADER_DELIMITER))
                .forEach(header -> mapping.put(header[HEADER_NAME_INDEX].trim(), header[HEADER_VALUE_INDEX].trim()));
    }

    public Headers(final Map<String, String> headers) {
        mapping.putAll(headers);
    }

    public String getHeaderValue(final String name) {
        return mapping.getOrDefault(name, null);
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
