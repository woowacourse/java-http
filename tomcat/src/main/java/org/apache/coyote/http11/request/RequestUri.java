package org.apache.coyote.http11.request;

public class RequestUri {
    private final String uri;

    private RequestUri(final String uri) {
        this.uri = uri;
    }

    public static RequestUri create(String uri) {
        return new RequestUri(uri);
    }

    public String getUri() {
        return uri;
    }
}
