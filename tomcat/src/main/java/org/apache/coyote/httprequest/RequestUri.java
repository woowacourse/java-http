package org.apache.coyote.httprequest;

public class RequestUri {

    private final String path;
    private final QueryString queryString;

    private RequestUri(final String path, final QueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestUri from(final String requestUri) {
        final int index = requestUri.indexOf("?");
        if (index == -1) {
            return new RequestUri(requestUri, QueryString.empty());
        }
        final String path = requestUri.substring(0, index);
        return new RequestUri(path, QueryString.from(requestUri.substring(index + 1)));
    }

    public String getPath() {
        return path;
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
