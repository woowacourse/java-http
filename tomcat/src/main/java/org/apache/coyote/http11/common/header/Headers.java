package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.ContentType;

public abstract class Headers {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Map<String, String> values;

    protected Headers() {
        values = new HashMap<>();
    }

    protected Headers(final Map<String, String> headers) {
        values = filterByHeaderType(headers);
    }

    private Map<String, String> filterByHeaderType(final Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> isType(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public abstract boolean isType(String headerName);

    protected String add(final HeaderName headerName, final String headerValue) {
        return values.put(headerName.getName(), headerValue);
    }

    protected boolean contains(final HeaderName headerName) {
        return values.containsKey(headerName.getName());
    }

    protected String find(final HeaderName headerName) {
        return values.get(headerName.getName());
    }

    @Override
    public String toString() {
        if (values.isEmpty()) {
            return "";
        }

        return values.entrySet()
                .stream()
                .map(entry -> format(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
    }

    private String format(final String headerName, final String value) {

        if (CONTENT_TYPE.getName().equals(headerName)) {
            return String.join(": ", headerName, ContentType.withCharset(value));
        }
        return String.join(": ", headerName, value);
    }
}
