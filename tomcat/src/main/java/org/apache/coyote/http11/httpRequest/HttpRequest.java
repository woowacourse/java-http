package org.apache.coyote.http11.httpRequest;

import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final RequestBody requestBody;

    public HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
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

    public Optional<String> findParamsValueFromBody(final String name) {
        return this.requestBody.findParamsValue(name);
    }
}
