package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpContentType;
import org.apache.coyote.http11.common.HttpHeaders;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseHeaders {
    private final Map<String, String> headers;

    private HttpResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders empty() {
        return new HttpResponseHeaders(new LinkedHashMap<>());
    }

    public void setContentType(final HttpContentType contentType) {
        headers.put(HttpHeaders.CONTENT_TYPE.getMessage(), contentType.message());
    }

    public void setContentLength(final int length) {
        headers.put(HttpHeaders.CONTENT_LENGTH.getMessage(), String.valueOf(length));
    }

    public void setLocation(final String location) {
        headers.put(HttpHeaders.LOCATION.getMessage(), location);
    }

    public void setCookie(final String cookie) {
        headers.put(HttpHeaders.SET_COOKIE.getMessage(), cookie);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
