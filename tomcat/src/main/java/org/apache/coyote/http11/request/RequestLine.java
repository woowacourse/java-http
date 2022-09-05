package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.http11.HttpMethod;

public class RequestLine {
    private static final String QUERY_STRING_LETTER = "?";
    private static final String REQUEST_LINE_SPLIT_REGEX = " ";
    private static final String QUERY_STRING_SPLIT_REGEX = "&";
    private static final String QUERY_SPLIT_REGEX = "=";

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryParams;
    private final String protocolVersion;

    private RequestLine(HttpMethod httpMethod, String path, Map<String, String> queryParams, String protocolVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParams = queryParams;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(String input) {
        String[] request = input.split(REQUEST_LINE_SPLIT_REGEX);

        HttpMethod method = HttpMethod.valueOf(request[0]);
        String path = parsePath(request[1]);
        Map<String, String> queryParams = parseQueryParams(request[1]);
        String protocolVersion = request[2];

        return new RequestLine(method, path, queryParams, protocolVersion);
    }

    private static String parsePath(String input) {
        if (!hasQueryParams(input)) {
            return input;
        }
        return input.substring(0, input.lastIndexOf(QUERY_STRING_LETTER));
    }

    private static boolean hasQueryParams(String input) {
        return input.contains(QUERY_STRING_LETTER);
    }

    private static Map<String, String> parseQueryParams(String input) {
        if (hasQueryParams(input)) {
            return readQueryParam(input);
        }
        return Map.of();
    }

    private static Map<String, String> readQueryParam(String input) {
        String queryString = input.substring(input.lastIndexOf(QUERY_STRING_LETTER) + 1);

        return Arrays.stream(queryString.split(QUERY_STRING_SPLIT_REGEX))
            .map(query -> query.split(QUERY_SPLIT_REGEX))
            .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }
}
