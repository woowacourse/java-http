package org.apache.coyote.http11.http.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    public static final String COOKIE = "Cookie";

    private final Map<String, String> headerValues;

    public RequestHeaders(final Map<String, String> headerValues) {
        this.headerValues = headerValues;
    }

    public static RequestHeaders from(final List<String> httpRequestMessage) {
        final Map<String, String> requestHeaders = new HashMap<>();
        for (final String header : httpRequestMessage) {
            final String[] splitHeader = header.split(HEADER_DELIMITER);
            requestHeaders.put(splitHeader[0], splitHeader[1]);
        }
        return new RequestHeaders(requestHeaders);
    }

    public String getValue(final String key) {
        return headerValues.get(key);
    }

    public Map<String, String> getHeaderValues() {
        return headerValues;
    }

    public boolean containsKey(final String key) {
        return headerValues.containsKey(key);
    }
}
