package org.apache.http;

public class RequestEntity {

    private final HttpMethod httpMethod;
    private final String uri;
    private final String queryString;

    public RequestEntity(final HttpMethod httpMethod, final String uri, final String queryString) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryString = queryString;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryString() {
        return queryString;
    }
}
