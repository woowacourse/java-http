package org.apache.coyote.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_DELIMITER = ": ";

    private static final int KEY = 0;
    private static final int VALUE = 1;

    private static final String EMPTY_CONTENT = "";

    private final Map<String, String> values;

    private HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();

        for (final String header : rawHeaders) {
            if (EMPTY_CONTENT.equals(header)) {
                break;
            }

            final String[] headerKeyValue = header.split(HEADER_DELIMITER);
            headers.put(headerKeyValue[KEY], headerKeyValue[VALUE].trim());
        }

        return new HttpHeaders(headers);
    }

    public String getHeader(final String header) {
        return values.get(header);
    }
}
