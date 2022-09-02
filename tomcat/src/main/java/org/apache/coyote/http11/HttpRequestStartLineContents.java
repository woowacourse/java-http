package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLineContents {

    private final String method;
    private final String url;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestStartLineContents(String method, String url, String httpVersion,
                                        final Map<String, String> queryParams) {
        this.method = method;
        this.url = url;
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

        final String[] urlAndQueryParams = strings[1].split("\\?");

        if (urlAndQueryParams.length >= 2) {
            saveQueryParams(urlAndQueryParams[1], queryParams);
        }

        return new HttpRequestStartLineContents(strings[0], urlAndQueryParams[0], strings[2], queryParams);
    }

    private static void saveQueryParams(final String queryParamString, final Map<String, String> queryParams) {
        final String[] keyAndValues = queryParamString.split("&");

        for (final String keyAndValue : keyAndValues) {
            final String[] strings = keyAndValue.split("=");
            queryParams.put(strings[0], strings[1]);
        }
    }

    public String getUrl() {
        return url;
    }
}
