package org.apache.coyote.http11;

public class StartLine {

    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;

    public StartLine(final HttpMethod httpMethod, final String uri, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
