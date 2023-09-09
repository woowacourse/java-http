package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";
    private final Map<String, String> headers = new LinkedHashMap<>();

    public ResponseHeaders addContentType(final String value) {
        headers.put(CONTENT_TYPE, value);
        return this;
    }

    public ResponseHeaders addContentLength(final String body) {
        if (!body.isBlank()) {
            headers.put(CONTENT_LENGTH, body.getBytes().length + " ");
        }
        return this;
    }

    public ResponseHeaders addLocation(final String location) {
        headers.put(LOCATION, location);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ResponseHeaders addCookie(final String value) {
        headers.put(SET_COOKIE, value);
        return this;
    }
}
