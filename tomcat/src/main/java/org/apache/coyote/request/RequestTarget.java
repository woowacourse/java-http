package org.apache.coyote.request;

public class RequestTarget {

    private final String path;
    private final String query;

    public RequestTarget(String path, String query) {
        this.path = path;
        this.query = query;
    }

    public static RequestTarget from(String rawTarget) {
        int queryStartIndex = rawTarget.indexOf('?');

        if (queryStartIndex == -1) {
            return new RequestTarget(rawTarget, "");
        }

        return new RequestTarget(
                rawTarget.substring(0, queryStartIndex),
                rawTarget.substring(queryStartIndex + 1)
        );
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }
}
