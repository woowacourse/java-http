package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpCookies;

public class HttpRequestHeader {

    private static final String HEADER_DELIMITER = ":";
    private static final int MAX_HEADER_PARTS = 2;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int DEFAULT_CONTENT_LENGTH = 0;

    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookies cookies;

    public HttpRequestHeader(final List<String> lines) {
        lines.stream()
                .map(line -> line.split(HEADER_DELIMITER, MAX_HEADER_PARTS))
                .filter(parts -> parts.length == MAX_HEADER_PARTS)
                .forEach(parts -> headers.put(parts[HEADER_KEY_INDEX].trim(), parts[HEADER_VALUE_INDEX].trim()));
        final String cookieLine = headers.get(COOKIE);
        cookies = new HttpCookies(cookieLine);
    }

    public HttpCookie getCookie(final String name) {
        return cookies.get(name);
    }

    public int getContentLength() {
        return Optional.ofNullable(headers.get(CONTENT_LENGTH))
                .map(Integer::parseInt)
                .orElse(DEFAULT_CONTENT_LENGTH);
    }

    public String get(String name) {
        return headers.get(name);
    }
}
