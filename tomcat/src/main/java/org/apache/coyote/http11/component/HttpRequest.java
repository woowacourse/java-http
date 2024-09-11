package org.apache.coyote.http11.component;

import java.net.URI;
import java.util.List;
import java.util.Objects;

public class HttpRequest implements HttpInteract {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Body body;

    public HttpRequest(final String plaintext) {
        Objects.requireNonNull(plaintext);
        final var lines = List.of(plaintext.split(LINE_DELIMITER));
        this.requestLine = new RequestLine(lines.getFirst());
        this.requestHeaders = new RequestHeaders(extractHeader(lines));
        this.body = new FormUrlEncodedBody(requestLine.getUri().getQuery());
    }

    public URI getUri() {
        return requestLine.getUri();
    }

    public Version getVersion() {
        return requestLine.getVersion();
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public RequestHeaders getHeaders() {
        return requestHeaders;
    }

    public Body getBody() {
        return body;
    }
}
