package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Query;

public class RequestTarget {

    private static final String PATH_QUERY_DELIMITER = "\\?";

    private final String path;
    private final String query;

    private RequestTarget(String path) {
        this.path = path;
        this.query = null;
    }

    private RequestTarget(String path, String query) {
        this.path = path;
        this.query = query;
    }

    public static RequestTarget create(String requestTarget) {
        String[] pathAndQuery = requestTarget.split(PATH_QUERY_DELIMITER);

        if (pathAndQuery.length == 2) {
            return new RequestTarget(pathAndQuery[0], pathAndQuery[1]);
        }
        return new RequestTarget(pathAndQuery[0]);
    }

    public String path() {
        return this.path;
    }

    public Query getQueries() {
        return Query.create(query);
    }
}
