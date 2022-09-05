package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.Regex;
import org.apache.coyote.http11.request.header.Headers;

public class Request {

    private final String path;
    private final QueryParams queryParams;
    private final Headers headers;

    public Request(final String path, final QueryParams queryParams, final Headers headers) {
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public static Request from(final List<String> requestLines) {
        final String uri = parseUri(requestLines);
        final Headers headers = Headers.from(requestLines.subList(1, requestLines.size()));

        final int queryStartIndex = uri.indexOf(Regex.QUERY_STRING.getValue());
        if (queryStartIndex < 0) {
            return new Request(uri, QueryParams.ofEmpty(), headers);
        }

        final String queryString = uri.substring(queryStartIndex + 1);
        return new Request(parsePath(uri, queryStartIndex), QueryParams.from(queryString), headers);
    }

    private static String parseUri(final List<String> requestLines) {
        return requestLines.get(0)
                .split(Regex.BLANK.getValue())[1];
    }

    private static String parsePath(final String uri, final int queryStartIndex) {
        return uri.substring(0, queryStartIndex);
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
