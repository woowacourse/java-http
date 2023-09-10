package org.apache.coyote.http11.request;

public class Uri {

    private final String path;
    private final QueryString queryString;

    public static Uri from(final String uri) {
        if (uri.contains("?")) {
            final int index = uri.indexOf("?");
            final String path = uri.substring(0, index);
            final String queryString = uri.substring(index + 1);
            return new Uri(path, queryString);
        }
        return new Uri(uri, "");
    }

    private Uri(final String path, final String queryString) {
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
