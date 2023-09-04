package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookie;

    public Cookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static Cookie from(final String input) {
        Map<String, String> holder = Arrays.stream(input.split("; "))
                .map(cookies -> cookies.split("="))
                .collect(Collectors.toMap(
                        cookie -> cookie[0],
                        cookie -> cookie[1])
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
        if (cookie.containsKey(JSESSIONID)) {
            return Optional.of(cookie.get(JSESSIONID));
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return cookie.entrySet().stream()
                .map(entry -> String.join("=",
                        entry.getKey(), entry.getValue())
                ).collect(Collectors.joining("; "));
    }
}
