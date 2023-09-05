package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.HttpHeaderType.CONTENT_LENGTH;

public class HttpHeaders {

    private final Map<String, String> headers = new HashMap<>();

    public String getContentLength() {
        return headers.get(CONTENT_LENGTH.getName());
    }

    public String getHeaderValue(final HttpHeaderType type) {
        return headers.get(type.getName());
    }

    public void setHeaderValue(final HttpHeaderType type, final String value) {
        headers.put(type.getName(), value);
    }

    public void setHeaderValue(final String type, final String value) {
        headers.put(type, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
