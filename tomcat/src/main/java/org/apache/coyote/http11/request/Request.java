package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.header.ContentType;

public class Request {

    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;

    public Request(final String path, final Map<String, String> queryParams, final Map<String, String> headers) {
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public static Request from(final List<String> requestLines) {
        final String uri = requestLines.get(0).split(" ")[1];
        final Map<String, String> header = parseHeaders(requestLines.subList(1, requestLines.size()));

        final int queryStartIndex = uri.indexOf("?");
        if (queryStartIndex < 0) {
            return new Request(uri, Map.of(), header);
        }

        final String queryString = uri.substring(queryStartIndex + 1);
        return new Request(parsePath(uri, queryStartIndex), parseQueryParams(queryString), header);
    }

    private static String parsePath(final String uri, final int queryStartIndex) {
        return uri.substring(0, queryStartIndex);
    }

    private static Map<String, String> parseQueryParams(final String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(param -> param.split("=", 2))
                .filter(param -> param.length == 2)
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
                ));
    }

    private static Map<String, String> parseHeaders(final List<String> headerLines) {
        return headerLines.stream()
                .map(line -> line.split(": ", 2))
                .collect(Collectors.toMap(
                        splitLine -> splitLine[0],
                        splitLine -> splitLine[1]
                ));
    }

    public boolean isPath(final String path) {
        return Objects.equals(this.path, path);
    }

    public boolean isForResource() {
        return path.contains(".");
    }

    public ContentType getContentType() {
        final String contentType = headers
                .getOrDefault("Accept", "text/html")
                .split(",")[0];

        return ContentType.of(contentType);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
