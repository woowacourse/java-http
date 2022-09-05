package org.apache.coyote.http11.request.requestline;

import java.util.Objects;
import org.apache.coyote.http11.Regex;
import org.apache.coyote.http11.request.QueryParams;

public class RequestUri {

    private final String path;
    private final QueryParams queryParams;


    private RequestUri(final String path, final QueryParams queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestUri from(String requestUri) {
        final int queryStartIndex = requestUri.indexOf(Regex.QUERY_STRING.getValue());
        if (queryStartIndex < 0) {
            return new RequestUri(requestUri, QueryParams.ofEmpty());
        }
        final String queryString = extractQueryString(requestUri, queryStartIndex);
        return new RequestUri(extractPath(requestUri, queryStartIndex), QueryParams.from(queryString));
    }

    private static String extractQueryString(final String requestUri, final int queryStartIndex) {
        return requestUri.substring(queryStartIndex + 1);
    }

    private static String extractPath(final String requestUri, final int queryStartIndex) {
        return requestUri.substring(0, queryStartIndex);
    }

    public boolean isPath(final String path) {
        return Objects.equals(this.path, path);
    }

    public boolean isForResource() {
        return path.contains(Regex.EXTENSION.getValue());
    }

    public String getPath() {
        return path;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }
}
