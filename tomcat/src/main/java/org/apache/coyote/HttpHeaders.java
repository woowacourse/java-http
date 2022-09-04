package org.apache.coyote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private static final String HEADER_DELIMITER = ": ";
    private static final String EMPTY_STRING = "";

    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> values;

    public HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();

        for (final String header : rawHeaders) {
            if (EMPTY_STRING.equals(header)) {
                break;
            }

            final String[] headerKeyValue = header.split(HEADER_DELIMITER);
            headers.put(headerKeyValue[KEY], headerKeyValue[VALUE].trim());
        }

        return new HttpHeaders(headers);
    }

    public void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public String getHeader(final String header) {
        return values.get(header);
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                .collect(Collectors.joining("\r\n"));
    }
}
