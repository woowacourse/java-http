package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestStartLine {

    private static final String DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int START_LINE_TOKEN_SIZE = 3;
    private static final String QUERY_PARAMETER_START_FLAG = "?";

    private final HttpRequestMethod httpRequestMethod;
    private final String requestURI;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    private HttpRequestStartLine(
            final HttpRequestMethod httpRequestMethod,
            final String requestURI,
            final String httpVersion,
            final Map<String, String> queryParams
    ) {
        this.httpRequestMethod = httpRequestMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public static HttpRequestStartLine from(final String requestLine) {
        final String[] startLineTokens = requestLine.split(DELIMITER);
        validateStartLineTokenSize(startLineTokens);
        final HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(startLineTokens[HTTP_METHOD_INDEX]);
        final String URIWithQueryStrings = startLineTokens[REQUEST_URI_INDEX];
        final String requestURI = parseURI(URIWithQueryStrings);
        final Map<String, String> queryParams = parseQueryParams(URIWithQueryStrings);
        final String httpVersion = startLineTokens[HTTP_VERSION_INDEX];
        return new HttpRequestStartLine(httpRequestMethod, requestURI, httpVersion, queryParams);
    }

    private static Map<String, String> parseQueryParams(final String URIWithQueryStrings) {
        if (URIWithQueryStrings.contains(QUERY_PARAMETER_START_FLAG)) {
            final int index = URIWithQueryStrings.indexOf(QUERY_PARAMETER_START_FLAG);
            final String params = URIWithQueryStrings.substring(index + 1);
            return Arrays.stream(params.split("&"))
                    .map(param -> param.split("="))
                    .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }
        return Map.of();
    }

    private static String parseURI(final String URIWithQueryStrings) {
        if (URIWithQueryStrings.contains(QUERY_PARAMETER_START_FLAG)) {
            final int index = URIWithQueryStrings.indexOf(QUERY_PARAMETER_START_FLAG);
            return URIWithQueryStrings.substring(0, index);
        }
        return URIWithQueryStrings;
    }

    private static void validateStartLineTokenSize(final String[] lines) {
        if (lines.length != START_LINE_TOKEN_SIZE) {
            throw new IllegalArgumentException("시작 라인의 토큰은 3개여야 합니다.");
        }
    }

    public HttpRequestMethod getHttpRequestMethod() {
        return httpRequestMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getParam(final String key) {
        return queryParams.get(key);
    }
}
