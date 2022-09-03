package org.apache.coyote.http11.request;

public class RequestLine {

    private final HttpMethod method;
    private final HttpUri uri;
    private final Http11Version httpVersion;

    private RequestLine(HttpMethod method, HttpUri uri, Http11Version httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(String requestLine) {
        String[] requests = requestLine.split(" ");

        String method = requests[0];
        String uri = requests[1];
        String version = requests[2];

        return new RequestLine(
                HttpMethod.of(method),
                HttpUri.of(uri),
                Http11Version.of(version)
        );
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpUri getUri() {
        return uri;
    }

    public Http11Version getHttpVersion() {
        return httpVersion;
    }
}
