package org.apache.coyote.http11;

public class HttpPath {

    private final String path;
    private final QueryString queryString;

    public static HttpPath from(final String uri) {
        if (uri.contains("?")) {
            final int index = uri.indexOf("?");
            final String path = uri.substring(0, index);
            final String queryString = uri.substring(index + 1);
            return new HttpPath(path, queryString);
        }
        return new HttpPath(uri, "");
    }

    private HttpPath(final String path, final String queryString) {
        this.path = path;
        this.queryString = QueryString.from(queryString);
    }

    public String getPath() {
        return path;
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
