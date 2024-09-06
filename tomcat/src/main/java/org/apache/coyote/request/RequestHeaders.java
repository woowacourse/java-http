package org.apache.coyote.request;

import java.util.Map;
import java.util.Optional;

public class RequestHeaders {

    private final Map<String, String> headers;

    private final RequestCookies cookies;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
        this.cookies = RequestCookies.from(headers.get("Cookie"));
    }

    public String contentLength() {
        return headers.get("Content-Length");
    }

    public Optional<String> findJSessionId() {
        return Optional.ofNullable(cookies.get("JSESSIONID"));
    }
}
