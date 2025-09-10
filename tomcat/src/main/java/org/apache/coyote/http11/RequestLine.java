package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class RequestLine {

    private static final String QUERY_STRING_START_DELIMITER = "?";

    private final String method;
    private final String uri;
    private final String uriPath;
    private final List<QueryParameter> queryParameters;
    private final String protocolVersion;

    public RequestLine(String requestLine) {
        try {
            String[] splitRequestLine = requestLine.split(" ");
            method = splitRequestLine[0];
            uri = splitRequestLine[1];
            protocolVersion = splitRequestLine[2];
            uriPath = parseRequestUriPath(uri);
            queryParameters = parseQueryParameters(uri);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("invalid request line: " + requestLine);
        }
    }

    private String parseRequestUriPath(String requestUri) {
        int queryStringIndex = requestUri.lastIndexOf(QUERY_STRING_START_DELIMITER);
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, requestUri.lastIndexOf(QUERY_STRING_START_DELIMITER));
    }

    private List<QueryParameter> parseQueryParameters(String requestUri) {
        int queryStringIndex = requestUri.lastIndexOf(QUERY_STRING_START_DELIMITER);
        if (queryStringIndex == -1) {
            return List.of();
        }
        String queryString = requestUri.substring(queryStringIndex + 1);
        return Arrays.stream(queryString.split("&"))
            .map(s -> s.split("="))
            .map(s -> (s.length == 2) ? new QueryParameter(s[0], s[1]) : new QueryParameter(s[0], null))
            .toList();
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getUriPath() {
        return uriPath;
    }

    public QueryParameter getQueryParameter(String name) {
        for (QueryParameter queryParameter : queryParameters) {
            if (queryParameter.name().equals(name)) {
                return queryParameter;
            }
        }
        return null;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
