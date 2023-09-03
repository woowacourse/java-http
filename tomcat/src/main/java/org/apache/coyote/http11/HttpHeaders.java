package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpHeaders {

    private static final int REQUEST_HEADER_KEY_INDEX = 0;
    private static final int REQUEST_HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new HashMap<>());
    }

    public static HttpHeaders of(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeaders = new HashMap<>();

        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            if (line == null) {
                throw new IllegalArgumentException("HTTP 헤더 값은 'null'일 수 없습니다.");
            }

            String[] requestHeaderParts = line.split(": ");

            requestHeaders.put(
                    requestHeaderParts[REQUEST_HEADER_KEY_INDEX].toLowerCase(),
                    requestHeaderParts[REQUEST_HEADER_VALUE_INDEX]
            );
        }

        return new HttpHeaders(requestHeaders);
    }

    public HttpHeaders add(String key, String value) {
        headers.put(key.toLowerCase(), value);
        return new HttpHeaders(headers);
    }

    public Set<Entry<String, String>> getEntrySet() {
        return headers.entrySet();
    }

    public String getValue(String key) {
        return headers.get(key.toLowerCase());
    }
}
