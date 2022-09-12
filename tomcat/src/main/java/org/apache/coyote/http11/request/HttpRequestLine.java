package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    public static final int METHOD_INDEX = 0;
    public static final int URI_INDEX = 1;
    public static final int VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String path;
    private final String httpVersion;
    private final String queryString;

    public HttpRequestLine(final HttpMethod httpMethod, final String path, final String httpVersion,
                           final String queryString) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.queryString = queryString;
    }

    public static HttpRequestLine of(final String rawRequestLine) {
        final String[] parsedLine = rawRequestLine.split(" ");

        final String method = parsedLine[METHOD_INDEX];
        final String uri = parsedLine[URI_INDEX];
        final String httpVersion = parsedLine[VERSION_INDEX];

        return new HttpRequestLine(HttpMethod.of(method), parseToPath(uri), httpVersion, parseToQueryString(uri));
    }

    private static String parseToPath(final String uri) {
        return uri.split("\\?")[0];
    }

    private static String parseToQueryString(final String uri) {
        if (hasQueryString(uri)) {
            final int index = uri.indexOf("?");
            return uri.substring(index + 1);
        }
        return "";
    }

    private static boolean hasQueryString(final String uri) {
        return uri.contains("?");
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getQueryString() {
        return queryString;
    }

    public boolean isGet() {
        return httpMethod.isGet();
    }
}
