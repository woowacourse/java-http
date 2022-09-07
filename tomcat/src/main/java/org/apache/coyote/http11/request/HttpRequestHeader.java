package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HttpRequestHeader {

    private static final String FORM_DATA_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    private HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeader of(final Queue<String> rawHeader) {
        final Map<String, String> parseHeaders = new HashMap<>();
        for (final String header : rawHeader) {
            final String[] parsedHeader = header.split(": ");
            parseHeaders.put(parsedHeader[0], parsedHeader[1].trim());
        }

        return new HttpRequestHeader(parseHeaders);
    }

    public boolean isFormDataType() {
        return headers.containsKey("Content-Type") && headers.get("Content-Type").equals(FORM_DATA_CONTENT_TYPE);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public boolean contains(final String headerName) {
        return headers.containsKey(headerName);
    }
}
