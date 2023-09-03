package org.apache.coyote.http11.request.line;

public class RequestLine {

    private final HttpMethod method;
    private final Path path;
    private final Protocol protocol;

    private RequestLine(HttpMethod method, Path path, Protocol protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public static RequestLine of(HttpMethod method, Path path, Protocol protocol) {
        return new RequestLine(method, path, protocol);
    }

    public HttpMethod method() {
        return method;
    }

    public Path path() {
        return path;
    }

    public Protocol protocol() {
        return protocol;
    }

}
