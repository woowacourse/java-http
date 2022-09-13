package org.apache.coyote.http11.request;

public class HttpRequestStartLine {

    private static final String QUERY_STRING_PREFIX = "?";

    private final HttpMethod method;
    private final String uri;

    public HttpRequestStartLine(final HttpMethod method, final String uri) {
        this.method = method;
        this.uri = uri;
    }

    public static HttpRequestStartLine from(final String startLine) {
        final String[] startLineContents = startLine.split(" ");

        return new HttpRequestStartLine(HttpMethod.valueOf(startLineContents[0]),
                takeUri(startLineContents[1]));
    }

    private static String takeUri(final String uriAndQueryParams) {
        if (!uriAndQueryParams.contains(QUERY_STRING_PREFIX)) {
            return uriAndQueryParams;
        }
        final int indexOfQueryStringPrefix = uriAndQueryParams.indexOf(QUERY_STRING_PREFIX);

        return uriAndQueryParams.substring(0, indexOfQueryStringPrefix);
    }
    

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
