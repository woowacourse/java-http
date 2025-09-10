package org.apache.coyote.http11.httpRequest;

import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader headers;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestLine requestLine,
            final RequestHeader headers,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public String getPath() {
        return this.requestLine.getPath();
    }

    public Optional<String> findParamsValueFromUri(final String name) {
        return this.requestLine.findParamsValue(name);
    }

    public ProtocolVersion getProtocolVersion() {
        return this.requestLine.getProtocolVersion();
    }

    public Optional<String> findParamsValueFromBody(final String name) {
        return this.requestBody.findParamsValue(name);
    }

    public Optional<String> findCookie() {
        return headers.findValue("Cookie");
    }
}
