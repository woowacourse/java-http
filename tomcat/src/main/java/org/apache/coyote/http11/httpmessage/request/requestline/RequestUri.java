package org.apache.coyote.http11.httpmessage.request.requestline;

public class RequestUri {

    private final String requestUri;

    public RequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestUri() {
        return requestUri;
    }
}
