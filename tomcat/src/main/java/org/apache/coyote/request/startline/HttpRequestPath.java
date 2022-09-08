package org.apache.coyote.request.startline;

public class HttpRequestPath {

    private final String uri;

    private HttpRequestPath(final String uri) {
        this.uri = uri;
    }

    public static HttpRequestPath from(final String uri) {
        return new HttpRequestPath(uri);
    }

    public String getUri() {
        return uri;
    }
}
