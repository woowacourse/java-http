package org.apache.coyote.http11.request;

public class RequestUri {

    public static final String SEPARATOR = "?";

    private final String path;

    private final String queryString;

    private RequestUri(final String path,
                       final String queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestUri from(final String requestUri) {
        if (requestUri.contains(SEPARATOR)) {
            String[] split = requestUri.split("\\" + SEPARATOR);
            return new RequestUri(split[0], split[1]);
        }
        return new RequestUri(requestUri, "");
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    @Override
    public String toString() {
        return "RequestUri{" +
                "path='" + path + '\'' +
                ", queryString='" + queryString + '\'' +
                '}';
    }
}
