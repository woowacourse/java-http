package org.apache.coyote.http.request;

public class RequestUri {

    private final String requestUri;

    public RequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public boolean contains(final String uri) {
        return requestUri.contains(uri);
    }

    public boolean endsWith(final String uri) {
        return requestUri.endsWith(uri);
    }

    public String getRequestUri() {
        return requestUri;
    }
}
