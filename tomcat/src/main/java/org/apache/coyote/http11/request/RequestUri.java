package org.apache.coyote.http11.request;

public class RequestUri {

    private final String path;
    private final String queryString;

    public RequestUri(final String requestUri) {
        final int queryIndex = requestUri.indexOf('?');
        if (queryIndex >= 0) {
            this.path = requestUri.substring(0, queryIndex);
            this.queryString = requestUri.substring(queryIndex + 1);
        } else {
            this.path = requestUri;
            this.queryString = "";
        }
    }

    public String getPath() {
        return path;
    }
}
