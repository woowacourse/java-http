package org.apache.coyote.http11.message;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final String httpMethod;
    private final String requestUri;
    private final Map<String, String> queryParams;
    private final HttpHeaders headers;

    public HttpRequest(String httpMethod, String requestUri, HttpHeaders headers) {
        this(httpMethod, requestUri, Map.of(), headers);
    }

    public HttpRequest(String httpMethod, String requestUri, Map<String, String> queryParams, HttpHeaders headers) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public static HttpRequest of(String startLine, HttpHeaders headers) throws IOException {
        String[] splitLine = startLine.split(" ");
        String httpMethod = splitLine[0];

        if (!containsQueryString(startLine)) {
            return new HttpRequest(httpMethod, splitLine[1], headers);
        }

        int queryDelimiterIndex = splitLine[1].indexOf("?");
        String requestUri = splitLine[1].substring(0, queryDelimiterIndex);
        String queryString = splitLine[1].substring(queryDelimiterIndex + 1);
        return new HttpRequest(httpMethod, requestUri, toQueryMap(queryString), headers);
    }

    private static boolean containsQueryString(String startLine) {
        return startLine.contains("?");
    }

    private static Map<String, String> toQueryMap(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public boolean isRoot() {
        return requestUri.equals("/");
    }
}
