package org.apache.coyote.http11;

public class HttpRequestLine {
    private final HttpMethod method;
    private final URI uri;
    private final String version;

    public HttpRequestLine(HttpMethod method, URI uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }
}
