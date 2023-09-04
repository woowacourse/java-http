package org.apache.coyote.http11.request;

public class HttpRequestUri {
    private final String uri;

    private HttpRequestUri(final String uri) {
        this.uri = uri;
    }

    public static HttpRequestUri from(String uri) {
        return new HttpRequestUri(uri);
    }

    public String getUri() {
        return uri;
    }
}
