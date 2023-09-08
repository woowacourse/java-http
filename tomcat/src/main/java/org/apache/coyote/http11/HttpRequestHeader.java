package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestHeader {

    private static final String HEADER_SEPARATOR = ": ";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_SEPARATOR = "=";
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> headers;

    public static HttpRequestHeader from(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();

        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] header = line.split(HEADER_SEPARATOR);
            headers.put(header[0], header[1]);

            line = bufferedReader.readLine();
        }

        return new HttpRequestHeader(headers);
    }

    private HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean hasJSessionId() {
        final String cookie = headers.get(COOKIE);
        if (cookie != null) {
            return Arrays.stream(cookie.split(COOKIE_DELIMITER))
                    .map(it -> it.split(COOKIE_SEPARATOR))
                    .anyMatch(it -> it[0].equals(JSESSIONID));
        }
        return false;
    }

    public String getJSessionId() {
        final String cookie = headers.get(COOKIE);
        return Arrays.stream(cookie.split(COOKIE_DELIMITER))
                .map(s -> s.split(COOKIE_SEPARATOR))
                .filter(word -> word[0].equals(JSESSIONID))
                .findAny()
                .map(word -> word[1])
                .orElse(null);
    }

    public String get(final String key) {
        return headers.get(key);
    }
}
