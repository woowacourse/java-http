package org.apache.coyote.http11.request;

public class Path {

    private final String path;
    private final QueryString queryString;

    public Path(String path, QueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static Path from(String path) {
        int queryStringIndex = path.indexOf("?");
        if (queryStringIndex == -1) {
            return new Path(path, QueryString.empty());
        }
        String queryString = path.substring(queryStringIndex + 1);
        path = path.substring(0, queryStringIndex);
        return new Path(path, QueryString.from(queryString));
    }

    public String getPath() {
        return path;
    }
}
