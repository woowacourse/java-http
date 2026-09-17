package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers) {
        this(requestLine, headers, null);
    }

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, byte[] body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Optional<String> getQueryParameter(String key) {
        return requestLine.getUri().findParameter(key);
    }
}
