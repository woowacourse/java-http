package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final String uriPath;
    private final QueryParams queryParams;
    private final HttpHeaders headers;

    public HttpRequest(final HttpMethod method, final String uriPath, final QueryParams queryParams,
                       final HttpHeaders headers) {
        this.method = method;
        this.uriPath = uriPath;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public static HttpRequest from(final String firstLine, final List<String> headers, final String requestBody) {
        final String[] splitFirstLine = firstLine.split(" ");

        final HttpMethod httpMethod = HttpMethod.from(splitFirstLine[0]);

        final String uriPathAndQueryString = splitFirstLine[1];
        final int queryStringFlagIndex = uriPathAndQueryString.indexOf("?");
        final String uriPath = extractUriPath(uriPathAndQueryString, queryStringFlagIndex);
        final QueryParams queryParams = extractQueryParams(uriPathAndQueryString, queryStringFlagIndex);

        return new HttpRequest(httpMethod, uriPath, queryParams, HttpHeaders.from(headers));
    }

    private static QueryParams extractQueryParams(final String uriPathAndQueryString, final int queryStringFlagIndex) {
        if (queryStringFlagIndex == -1) {
            return QueryParams.empty();
        }
        return QueryParams.from(uriPathAndQueryString.substring(queryStringFlagIndex + 1));
    }

    private static String extractUriPath(final String uriPathAndQueryString, final int queryStringFlagIndex) {
        if (queryStringFlagIndex == -1) {
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
