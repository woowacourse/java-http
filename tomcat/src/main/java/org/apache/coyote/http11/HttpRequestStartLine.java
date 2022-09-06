package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLine {

    private static final String QUERY_STRING_PREFIX = "?";

    private final HttpMethod method;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestStartLine(final HttpMethod method, final String uri, final String httpVersion,
                                final Map<String, String> queryParams) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public static HttpRequestStartLine from(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("잘못된 입력입니다.");
        }

        final String[] startLineContents = startLine.split(" ");

        return new HttpRequestStartLine(HttpMethod.findMethod(startLineContents[0]),
                takeUri(startLineContents[1]),
                startLineContents[2],
                takeQueryParams(startLineContents[1]));
    }

    private static String takeUri(final String uriAndQueryParams) {
        if (!uriAndQueryParams.contains(QUERY_STRING_PREFIX)) {
            return uriAndQueryParams;
        }
        final int indexOfQueryStringPrefix = uriAndQueryParams.indexOf(QUERY_STRING_PREFIX);

        return uriAndQueryParams.substring(0, indexOfQueryStringPrefix);
    }

    private static Map<String, String> takeQueryParams(final String uriAndQueryParams) {
        final Map<String, String> queryParams = new HashMap<>();

        if (!uriAndQueryParams.contains(QUERY_STRING_PREFIX)) {
            return queryParams;
        }

        final int indexOfQueryStringPrefix = uriAndQueryParams.indexOf(QUERY_STRING_PREFIX);

        return parseQueryParams(uriAndQueryParams.substring(indexOfQueryStringPrefix + 1));
    }

    private static Map<String, String> parseQueryParams(final String queryParams) {
        final Map<String, String> result = new HashMap<>();
        final String[] keyAndValues = queryParams.split("&");

        for (final String keyValue : keyAndValues) {
            final String[] keyAndValue = keyValue.split("=");
            result.put(keyAndValue[0], keyAndValue[1]);
        }
        return result;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
