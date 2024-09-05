package org.apache.coyote.http11;

public class RequestUri {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String EMPTY_STRING = "";

    private final String path;
    private final String queryString;

    public RequestUri(String path, String queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestUri from(String requestUri) {
        int index = requestUri.indexOf(QUERY_STRING_DELIMITER);
        if (index == -1) {
            return new RequestUri(requestUri, EMPTY_STRING);
        }
        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);

        return new RequestUri(path, queryString);
    }

    public String getRequestUri() {
        return String.join(EMPTY_STRING, path, queryString);
    }
}
