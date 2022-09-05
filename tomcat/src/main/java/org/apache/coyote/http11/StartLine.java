package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StartLine {

    private final String httpMethod;
    private final String requestUri;
    private final Map<String, String> queryParams;

    public StartLine(String httpMethod, String requestUri) {
        this(httpMethod, requestUri, Map.of());
    }

    public StartLine(String httpMethod, String requestUri, Map<String, String> queryParams) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.queryParams = queryParams;
    }

    public static StartLine of(String startLine) {
        String[] splitLine = startLine.split(" ");
        String httpMethod = splitLine[0];

        if (!containsQueryString(startLine)) {
            return new StartLine(httpMethod, splitLine[1]);
        }

        int queryDelimiterIndex = splitLine[1].indexOf("?");
        String requestUri = splitLine[1].substring(0, queryDelimiterIndex);
        String queryString = splitLine[1].substring(queryDelimiterIndex + 1);
        return new StartLine(httpMethod, requestUri, toQueryMap(queryString));
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
}
