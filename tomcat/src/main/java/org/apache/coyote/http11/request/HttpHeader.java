package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HttpHeader {

    private final Map<String, String> headers;

    private HttpHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader of(final Queue<String> rawHeader) {
        final Map<String, String> parseHeaders = new HashMap<>();
        for (final String header : rawHeader) {
            final String[] parsedHeader = header.split(": ");
            parseHeaders.put(parsedHeader[0], parsedHeader[1]);
        }

        return new HttpHeader(parseHeaders);
    }

    public void addHeader(final String headerName, final String value) {
        headers.put(headerName, value);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }
}
