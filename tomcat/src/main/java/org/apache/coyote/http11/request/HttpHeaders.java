package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {
    public static final String HEADER_DELIMITER = ": ";
    public static final String COOKIE_KEY = "Cookie";
    public static final int MINIMUM_LINE_SIZE = 2;

    private final Map<String, String> value;
    private final HttpCookie cookie;

    public HttpHeaders(Map<String, String> value) {
        this.value = value;
        this.cookie = HttpCookie.from(value.get(COOKIE_KEY));
    }

    public static HttpHeaders from(List<String> lines) {
        validateLineSize(lines);
        final Map<String, String> buffer = new HashMap<>();
        int startIndex = 1;
        for (int index = startIndex; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.isEmpty()) {
                break;
            }
            String[] keyValue = line.split(HEADER_DELIMITER);
            validate(keyValue);
            String headerKey = keyValue[0];
            String headerValue = keyValue[1];
            buffer.put(headerKey, headerValue);
        }

        return new HttpHeaders(buffer);
    }

    private static void validateLineSize(List<String> lines) {
        if (lines.size() < MINIMUM_LINE_SIZE) {
            throw new IllegalArgumentException("request must be more than two lines");
        }
    }

    private static void validate(String[] split) {
        if (split.length < MINIMUM_LINE_SIZE) {
            throw new IllegalArgumentException("key value not matched");
        }
        if (split[0].isBlank() || split[1].isBlank()) {
            throw new IllegalArgumentException("key or value cant not be blank");
        }
    }

    public String getCookieValue(String key) {
        return cookie.getValue(key);
    }

    public boolean contains(String key) {
        return value.containsKey(key);
    }

    public String find(String key) {
        return value.get(key);
    }

    public void set(String key, String value) {
        this.value.put(key, value);
    }
}
