package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = "; ";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookies) {
        String[] values = cookies.split(COOKIE_DELIMITER);
        return Arrays.stream(values)
                .map(value -> value.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.collectingAndThen(
                        Collectors.toUnmodifiableMap(split -> split[0], split -> split[1]),
                        HttpCookie::new
                ));
    }

    public static HttpCookie ofJSessionId(String sessionId) {
        return new HttpCookie(Map.of(JSESSIONID, sessionId));
    }

    public String getJSessionId() {
        return cookies.getOrDefault(JSESSIONID, "");
    }

    public String getCookieToMessage() {
        return cookies.entrySet().stream()
                .map(entry -> String.join("=", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
    }
}
