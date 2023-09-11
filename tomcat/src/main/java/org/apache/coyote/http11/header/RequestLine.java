package org.apache.coyote.http11.header;

import java.net.URI;

public class RequestLine {
    private final HttpMethod method;
    private final URI uri;
    private final HttpVersion version;

    public RequestLine(final HttpMethod method, final URI uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine parse(final String requestLine) {
        final var tokens = requestLine.split(" ");
        return new RequestLine(
                HttpMethod.of(tokens[0]),
                URI.create(tokens[1]),
                HttpVersion.of(tokens[2])
        );
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
