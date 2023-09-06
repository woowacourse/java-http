package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.common.Constant.COOKIE_DELIMITER;
import static org.apache.coyote.http11.common.Constant.COOKIE_SEPARATOR;

public class Cookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookie;

    public Cookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static Cookie from(final String input) {
        Map<String, String> holder = Arrays.stream(input.split(COOKIE_DELIMITER))
                .map(cookies -> cookies.split(COOKIE_SEPARATOR))
                .collect(Collectors.toMap(
                        cookie -> cookie[KEY_INDEX],
                        cookie -> cookie[VALUE_INDEX])
                );

        return new Cookie(holder);
    }

    public static Cookie fromUserJSession(final String sessionId) {
        return new Cookie(Map.of(JSESSIONID, sessionId));
    }

    public static Cookie createDefault() {
        return new Cookie(Map.of());
    }

    public Optional<String> getJSessionId() {
        return Optional.ofNullable(cookie.get(JSESSIONID));
    }

    @Override
    public String toString() {
        return cookie.entrySet()
                .stream()
                .map(entry -> String.join(
                        COOKIE_SEPARATOR,
                        entry.getKey(),
                        entry.getValue()
                )).collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
