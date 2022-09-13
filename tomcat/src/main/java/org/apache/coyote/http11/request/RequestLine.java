package org.apache.coyote.http11.request;

import java.util.Objects;
import org.apache.coyote.http11.HttpMethod;

public class RequestLine {

    private static final String FIRST_LINE_SEPARATOR = " ";
    private static final int FIRST_LINE_METHOD_INDEX = 0;
    private static final int FIRST_LINE_URI_INDEX = 1;
    private static final String QUERY_STRING_START_FLAG = "?";
    private static final int INDEX_NOT_FOUND = -1;

    private final HttpMethod method;
    private final String uriPath;
    private final QueryParams queryParams;

    private RequestLine(final HttpMethod method, final String uriPath, final QueryParams queryParams) {
        this.method = method;
        this.uriPath = uriPath;
        this.queryParams = queryParams;
    }

    public static RequestLine from(final String httpMessageFirstLine) {
        final String[] splitFirstLine = httpMessageFirstLine.split(FIRST_LINE_SEPARATOR);

        final HttpMethod httpMethod = HttpMethod.from(splitFirstLine[FIRST_LINE_METHOD_INDEX]);

        final String uriPathAndQueryString = splitFirstLine[FIRST_LINE_URI_INDEX];
        final int queryStringFlagIndex = uriPathAndQueryString.indexOf(QUERY_STRING_START_FLAG);
        final String uriPath = extractUriPath(uriPathAndQueryString, queryStringFlagIndex);
        final String queryString = extractQueryString(uriPathAndQueryString, queryStringFlagIndex);

        return new RequestLine(httpMethod, uriPath, QueryParams.from(queryString));
    }

    private static String extractUriPath(final String uriPathAndQueryString, final int queryStringFlagIndex) {
        if (queryStringFlagIndex == INDEX_NOT_FOUND) {
            return uriPathAndQueryString;
        }
        return uriPathAndQueryString.substring(0, queryStringFlagIndex);
    }

    private static String extractQueryString(final String uriPathAndQueryString, final int queryStringFlagIndex) {
        if (queryStringFlagIndex == INDEX_NOT_FOUND) {
            return "";
        }
        return uriPathAndQueryString.substring(queryStringFlagIndex + 1);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUriPath() {
        return uriPath;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLine)) {
            return false;
        }
        final RequestLine that = (RequestLine) o;
        return getMethod() == that.getMethod() && Objects.equals(getUriPath(), that.getUriPath())
                && Objects.equals(getQueryParams(), that.getQueryParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getUriPath(), getQueryParams());
    }
}
