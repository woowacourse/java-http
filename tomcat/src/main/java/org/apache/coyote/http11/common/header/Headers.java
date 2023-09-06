package org.apache.coyote.http11.common.header;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Headers {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Map<HeaderName, String> values;

    protected Headers() {
        values = new HashMap<>();
    }

    protected Headers(final Map<HeaderName, String> headers) {
        values = filterByHeaderType(headers);
    }

    private Map<HeaderName, String> filterByHeaderType(final Map<HeaderName, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> isType(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public abstract boolean isType(HeaderName headerName);

    protected boolean contains(final HeaderName headerName) {
        return values.containsKey(headerName);
    }

    protected String find(final HeaderName headerName) {
        return values.get(headerName);
    }

    protected String add(final HeaderName headerName, final String headerValue) {
        return values.put(headerName, headerValue);
    }

    @Override
    public String toString() {
        if (values.isEmpty()) {
            return "";
        }

        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey().getName() + ": " + entry.getValue())
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
    }
}
