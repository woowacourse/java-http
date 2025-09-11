package org.apache.coyote.http;

import com.techcourse.exception.UncheckedServletException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookies) {
        return new HttpCookie(convertToHttpCookieMap(cookies));
    }

    private static Map<String, String> convertToHttpCookieMap(final String cookieString) {
        Map<String, String> httpCookieMap = new ConcurrentHashMap<>();

        if (cookieString == null || cookieString.isEmpty()) {
            return httpCookieMap;
        }

        String[] httpCookiePairs = cookieString.split("; ");
        for (String httpCookiePair : httpCookiePairs) {
            String[] keyValuePair = httpCookiePair.split("=");
            validateKeyValuePair(keyValuePair);
            httpCookieMap.put(keyValuePair[0], keyValuePair[1]);
        }

        return httpCookieMap;
    }

    private static void validateKeyValuePair(final String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            throw new UncheckedServletException("쿠키의 형식은 'key=value' 이여야 합니다.");
        }
    }

    public static HttpCookie empty() {
        return new HttpCookie(new ConcurrentHashMap<>());
    }

    public boolean hasEmptySessionId() {
        return !values.containsKey("JSESSIONID");
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void addSessionId(final String sessionId) {
        values.put("JSESSIONID", sessionId);
    }

    public String toHttpHeaderFormat() {
        return String.format("Set-Cookie: %s ", values.entrySet()
                .stream()
                .map(value -> value.getKey() + "=" + value.getValue())
                .collect(Collectors.joining("; ")));
    }

    public String getJSessionId() {
        return values.get("JSESSIONID");
    }
}
