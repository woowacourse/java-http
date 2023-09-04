package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaders {

    private static final int REQUEST_HEADER_KEY_INDEX = 0;
    private static final int REQUEST_HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private HttpRequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders of(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeaders = new HashMap<>();

        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            if (line == null) {
                throw new IllegalArgumentException("HTTP 헤더 값은 'null'일 수 없습니다.");
            }

            String[] requestHeaderParts = line.split(":");

            requestHeaders.put(
                    requestHeaderParts[REQUEST_HEADER_KEY_INDEX].strip().toLowerCase(),
                    requestHeaderParts[REQUEST_HEADER_VALUE_INDEX].strip()
            );
        }

        return new HttpRequestHeaders(requestHeaders);
    }

    public HttpRequestHeaders add(String key, String value) {
        headers.put(key.toLowerCase(), value);
        return new HttpRequestHeaders(headers);
    }

    public String getValue(String key) {
        return headers.get(key.toLowerCase());
    }
}
