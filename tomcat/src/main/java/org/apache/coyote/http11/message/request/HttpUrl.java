package org.apache.coyote.http11.message.request;

public class HttpUrl {
    private final String path;
    private final QueryParameters queryParameters;

    public HttpUrl(String path, QueryParameters queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public String getPath() {
        return path;
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }
}
