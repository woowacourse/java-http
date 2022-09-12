package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String requestUri;
    private final Map<String, String> queryParams;

    public RequestLine(HttpMethod httpMethod, String requestUri, Map<String, String> queryParams) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.queryParams = queryParams;
    }

    public static RequestLine of(String startLine) {
        String[] splitLine = startLine.split(" ");
        HttpMethod httpMethod = HttpMethod.valueOf(splitLine[0]);

        if (!containsQueryString(startLine)) {
            return new RequestLine(httpMethod, splitLine[1], new HashMap<>());
        }

        int queryDelimiterIndex = splitLine[1].indexOf("?");
        String requestUri = splitLine[1].substring(0, queryDelimiterIndex);
        String queryString = splitLine[1].substring(queryDelimiterIndex + 1);
        return new RequestLine(httpMethod, requestUri, toQueryMap(queryString));
    }

    private static boolean containsQueryString(String startLine) {
        return startLine.contains("?");
    }

    private static Map<String, String> toQueryMap(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
