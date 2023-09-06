package org.apache.coyote.http;

public class StartLine {

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;

    private final Protocol protocol;

    public StartLine(final HttpMethod httpMethod, final RequestUri requestUri, final Protocol protocol) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.protocol = protocol;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public boolean isPostMethod() {
        return httpMethod == HttpMethod.POST;
    }

    public boolean isGetMethod() {
        return httpMethod == HttpMethod.GET;
    }

    public boolean containsRequestUri(final String uri) {
        return requestUri.contains(uri);
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }
}
