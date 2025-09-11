package org.apache.coyote.http11;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookie {

    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookie;

    public Cookie(String cookieHeader) {
        this.cookie = parse(cookieHeader);
    }

    public static String ofJSessionId(String sessionId) {
        return "JSESSIONID=" + sessionId + "; Path=/";
    }

    private Map<String, String> parse(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return Map.of();
        }
        return Stream.of(cookieHeader.split(";"))
                .map(String::trim)
                .map(cookie -> cookie.split(KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }

    public String getCookie(String name) {
        return cookie.get(name);
    }
}
