package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLine {

    private static final String QUERY_STRING_PREFIX = "?";

    private final String method;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestStartLine(String method, String uri, String httpVersion,
                                final Map<String, String> queryParams) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public static HttpRequestStartLine from(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        final Map<String, String> queryParams = new HashMap<>();

        if (startLine == null) {
            throw new IllegalArgumentException("잘못된 입력입니다.");
        }

        final String[] startLineContents = startLine.split(" ");

        final String uri = parseUri(startLineContents[1], queryParams);

        return new HttpRequestStartLine(startLineContents[0], uri, startLineContents[2], queryParams);
    }

    private static String parseUri(final String uriAndQueryParams, final Map<String, String> queryParams) {
        if (!uriAndQueryParams.contains(QUERY_STRING_PREFIX)) {
            return uriAndQueryParams;
        }
        final int indexOfQueryStringPrefix = uriAndQueryParams.indexOf(QUERY_STRING_PREFIX);

        saveQueryParams(uriAndQueryParams.substring(indexOfQueryStringPrefix + 1), queryParams);
        return uriAndQueryParams.substring(0, indexOfQueryStringPrefix);
    }

    private static void saveQueryParams(final String queryParamString, final Map<String, String> queryParams) {
        final String[] keyAndValues = queryParamString.split("&");

        for (final String keyValue : keyAndValues) {
            final String[] keyAndValue = keyValue.split("=");
            queryParams.put(keyAndValue[0], keyAndValue[1]);
        }
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
