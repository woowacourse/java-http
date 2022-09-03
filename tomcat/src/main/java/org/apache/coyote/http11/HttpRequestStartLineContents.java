package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLineContents {

    private static final String QUERY_STRING_PREFIX = "?";

    private final String method;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestStartLineContents(String method, String uri, String httpVersion,
                                        final Map<String, String> queryParams) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public static HttpRequestStartLineContents from(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        final Map<String, String> queryParams = new HashMap<>();

        if (startLine == null) {
            throw new IllegalArgumentException("잘못된 입력입니다.");
        }

        final String[] strings = startLine.split(" ");

        final String uri = parseUri(strings[1], queryParams);

        return new HttpRequestStartLineContents(strings[0], uri, strings[2], queryParams);
    }

    private static String parseUri(final String uriAndQueryParams, final Map<String, String> queryParams) {
        if (!uriAndQueryParams.contains(QUERY_STRING_PREFIX)) {
            return uriAndQueryParams;
        }
        final int indexOfQueryStringPrefix = uriAndQueryParams.indexOf(QUERY_STRING_PREFIX);

        saveQueryParams(uriAndQueryParams.substring(indexOfQueryStringPrefix), queryParams);
        return uriAndQueryParams.substring(0, indexOfQueryStringPrefix);
    }

    private static void saveQueryParams(final String queryParamString, final Map<String, String> queryParams) {
        final String[] keyAndValues = queryParamString.split("&");

        for (final String keyAndValue : keyAndValues) {
            final String[] strings = keyAndValue.split("=");
            queryParams.put(strings[0], strings[1]);
        }
    }

    public String getUri() {
        return uri;
    }
}
