package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {
    private static final String COOKIE_DELIMITER = ";";
    private static final String NAME_VALUE_DELIMITER = "=";
    public static final String JSESSIONID = "JSESSIONID";
    private static final int NOT_FOUND = -1;

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String rawCookie) {
        final Map<String, String> parsed = new HashMap<>();
        for (final String pair : rawCookie.split(COOKIE_DELIMITER)) {
            final String trimmed = pair.strip();
            if (trimmed.isEmpty()) {
                continue;
            }
            final int delimiterIndex = trimmed.indexOf(NAME_VALUE_DELIMITER);
            if (delimiterIndex == NOT_FOUND) {
                continue;
            }
            final String name = trimmed.substring(0, delimiterIndex);
            if (name.isEmpty()) {
                continue;
            }
            final String value = trimmed.substring(delimiterIndex + 1);
            // 같은 이름이면 첫 번째를 쓴다. 브라우저는 Path가 더 구체적인 쿠키를 먼저 보낸다 (RFC 6265 5.4)
            parsed.putIfAbsent(name, value);
        }
        return new HttpCookie(Map.copyOf(parsed));
    }

    public Optional<String> get(final String name) {
        return Optional.ofNullable(cookies.get(name));
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }
}
