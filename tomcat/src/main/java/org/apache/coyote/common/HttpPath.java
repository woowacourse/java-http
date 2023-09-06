package org.apache.coyote.common;

import org.apache.coyote.util.QueryParser;

public class HttpPath {

    private final String path;
    private final QueryString queryString;

    private HttpPath(String path) {
        this.path = path;
        this.queryString = QueryString.EMPTY;
    }

    private HttpPath(String path, QueryString queryStrings) {
        this.path = path;
        this.queryString = queryStrings;
    }

    public static HttpPath from(String uri) {
        int index = uri.indexOf("?");
        if (index <= 0) {
            return new HttpPath(uri);
        }
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        return new HttpPath(path, QueryParser.parse(queryString));
    }

    public String getPath() {
        return path;
    }

    public String getQueryString(String key) {
        return queryString.get(key);
    }

    public QueryString getQueryStrings() {
        return queryString;
    }
}
