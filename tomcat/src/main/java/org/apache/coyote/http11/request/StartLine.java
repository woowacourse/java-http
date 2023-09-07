package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;

public class StartLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int URI_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String URL_SEPARATOR = "\\?";

    private final HttpMethod method;
    private final String uri;
    private final QueryString queryString;
    private final HttpVersion version;

    private StartLine(final HttpMethod method, final String uri, final QueryString queryString, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.queryString = queryString;
    }

    public static StartLine from(final String request) {
        final String[] startLine = request.split(SEPARATOR);
        final HttpMethod httpMethod = HttpMethod.findBy(startLine[METHOD_INDEX]);
        final String[] urls = parseUrl(startLine[URL_INDEX]);
        final QueryString queryString = convertFrom(urls[QUERY_STRING_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findBy(startLine[VERSION_INDEX]);

        return new StartLine(httpMethod, urls[URI_INDEX], queryString, httpVersion);
    }

    private static String[] parseUrl(final String url) {
        if (url.contains(QUERY_STRING_SEPARATOR)) {
            return url.split(URL_SEPARATOR);
        }

        return new String[]{url, null};
    }

    private static QueryString convertFrom(String queryString) {
        if (queryString == null) {
            return null;
        }

        return QueryString.from(queryString);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
