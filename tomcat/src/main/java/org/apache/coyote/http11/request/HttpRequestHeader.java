package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.response.header.Cookie;

public class HttpRequestHeader {

    private static final String HTTP_REQUEST_HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String CONTENT_LENGTH_KEY = "Content-Length";
    private static final String COOKIE_KEY = "Cookie";

    private final Map<String, String> values;
    private final Cookie cookie;

    public HttpRequestHeader(Map<String, String> values, Cookie cookie) {
        this.values = values;
        this.cookie = cookie;
    }

    public static HttpRequestHeader from(List<String> requestHeaderLines) {
        Map<String, String> headers = new ConcurrentHashMap<>();
        for (String line : requestHeaderLines) {
            String[] keyAndValue = line.split(HTTP_REQUEST_HEADER_KEY_VALUE_DELIMITER);
            headers.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new HttpRequestHeader(headers, parseCookieHeaderIfExists(headers));
    }

    private static Cookie parseCookieHeaderIfExists(Map<String, String> headers) {
        if (headers.containsKey(COOKIE_KEY)) {
            String cookieValues = headers.get(COOKIE_KEY);
            return Cookie.fromRequest(cookieValues);
        }
        return Cookie.empty();
    }

    public boolean hasContentLength() {
        return values.keySet()
                .stream()
                .anyMatch(CONTENT_LENGTH_KEY::equals);
    }

    public String getValueOf(String key) {
        return values.get(key);
    }

    public boolean hasCookieOf(String cookieName) {
        return cookie.containsCookieOf(cookieName);
    }

    public String getCookieOf(String cookieName) {
        return cookie.getCookieOf(cookieName);
    }
}
