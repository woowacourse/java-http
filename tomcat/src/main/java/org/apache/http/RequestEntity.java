package org.apache.http;

public class RequestEntity {

    private final String uri;
    private final String queryString;

    public RequestEntity(final String uri, final String queryString) {
        this.uri = uri;
        this.queryString = queryString;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryString() {
        return queryString;
    }
}
