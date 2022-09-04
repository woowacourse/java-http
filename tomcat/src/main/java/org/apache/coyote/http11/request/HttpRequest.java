package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private static final String FIRST_LINE_SEPARATOR = " ";
    private static final int FIRST_LINE_METHOD_INDEX = 0;
    private static final int FIRST_LINE_URI_INDEX = 1;
    private static final String QUERY_STRING_START_FLAG = "?";
    private static final int INDEX_NOT_FOUND = -1;

    private final HttpMethod method;
    private final String uriPath;
    private final QueryParams queryParams;
    private final HttpHeaders headers;

    private HttpRequest(final HttpMethod method,
                        final String uriPath,
                        final QueryParams queryParams,
                        final HttpHeaders headers) {
        this.method = method;
        this.uriPath = uriPath;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public static HttpRequest from(final String firstLine, final List<String> headers, final String requestBody) {
        final String[] splitFirstLine = firstLine.split(FIRST_LINE_SEPARATOR);

        final HttpMethod httpMethod = HttpMethod.from(splitFirstLine[FIRST_LINE_METHOD_INDEX]);

        final String uriPathAndQueryString = splitFirstLine[FIRST_LINE_URI_INDEX];
        final int queryStringFlagIndex = uriPathAndQueryString.indexOf(QUERY_STRING_START_FLAG);
        final String uriPath = extractUriPath(uriPathAndQueryString, queryStringFlagIndex);
        final QueryParams queryParams = extractQueryParams(uriPathAndQueryString, queryStringFlagIndex);

        return new HttpRequest(httpMethod, uriPath, queryParams, HttpHeaders.from(headers));
    }

    private static QueryParams extractQueryParams(final String uriPathAndQueryString, final int queryStringFlagIndex) {
        if (queryStringFlagIndex == INDEX_NOT_FOUND) {
            return QueryParams.empty();
        }
        return QueryParams.from(uriPathAndQueryString.substring(queryStringFlagIndex + 1));
    }

    private static String extractUriPath(final String uriPathAndQueryString, final int queryStringFlagIndex) {
        if (queryStringFlagIndex == INDEX_NOT_FOUND) {
            return uriPathAndQueryString;
        }
        return uriPathAndQueryString.substring(0, queryStringFlagIndex);
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

    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpRequest)) {
            return false;
        }
        final HttpRequest that = (HttpRequest) o;
        return method == that.method
                && Objects.equals(uriPath, that.uriPath)
                && Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uriPath, queryParams);
    }
}
