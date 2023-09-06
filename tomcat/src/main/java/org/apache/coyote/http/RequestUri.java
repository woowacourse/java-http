package org.apache.coyote.http;

public class RequestUri {

    private final String requestUri;

    public RequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public boolean contains(final String uri) {
        return requestUri.contains(uri);
    }

    public String getRequestUri() {
        return requestUri;
    }
}
