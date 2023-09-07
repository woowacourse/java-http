package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;

import java.util.Optional;

public class StartLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String URI_SEPARATOR = "\\?";

    private final HttpMethod method;
    private final String path;
    private final QueryString queryString;
    private final HttpVersion version;

    private StartLine(final HttpMethod method, final String path, final QueryString queryString, final HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryString = queryString;
    }

    public static StartLine from(final String request) {
        final String[] startLine = request.split(SEPARATOR);
        final HttpMethod httpMethod = HttpMethod.findBy(startLine[METHOD_INDEX]);
        final String[] uri = parseUri(startLine[URI_INDEX]);
        final QueryString queryString = convertFrom(uri[QUERY_STRING_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findBy(startLine[VERSION_INDEX]);

        return new StartLine(httpMethod, uri[PATH_INDEX], queryString, httpVersion);
    }

    private static String[] parseUri(final String uri) {
        if (uri.contains(QUERY_STRING_SEPARATOR)) {
            return uri.split(URI_SEPARATOR);
        }

        return new String[]{uri, null};
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

    public String getPath() {
        return path;
    }

    public Optional<QueryString> getQueryString() {
        return Optional.ofNullable(queryString);
    }

    public HttpVersion getVersion() {
        return version;
    }
}
