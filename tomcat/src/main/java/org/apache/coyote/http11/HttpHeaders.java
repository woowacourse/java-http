package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(
            final String name,
            final String value
    ) {
        headers.put(name, value);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public static HttpHeaders parseHeaders(final List<String> headerLines) {
        final HttpHeaders headers = new HttpHeaders();
        for (final String rawHeader : headerLines) {
            final String[] rawHeaders = rawHeader.split(": ");
            headers.addHeader(rawHeaders[0], rawHeaders[1]);
        }

        return headers;
    }
}
