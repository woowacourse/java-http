package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpMethod method;
    private final String requestUri;
    private final HttpHeaders headers;

    public HttpRequest(final HttpMethod method, final String requestUri, final HttpHeaders headers) {
        this.method = method;
        this.requestUri = requestUri;
        this.headers = headers;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getContentType() {
        return headers.getContentType();
    }
}
