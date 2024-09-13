package org.apache.coyote.http;

public class HttpRequestUri implements HttpComponent {

    public static final String QUERY_SEPARATOR = "?";

    private final String uri;
    private final String path;
    private final String queryString;
    private final HttpQueryParams queryParams;

    public HttpRequestUri(final String uri) {
        this.uri = uri;
        int index = uri.lastIndexOf(QUERY_SEPARATOR);
        path = (index == -1) ? uri : uri.substring(0, index);
        queryString = (index == -1) ? "" : uri.substring(index + 1);
        queryParams = new HttpQueryParams(queryString);
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public HttpQueryParams getQueryParams() {
        return queryParams;
    }

    @Override
    public String asString() {
        return uri;
    }
}
