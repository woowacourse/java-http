package org.apache.coyote.http11.message.request.header;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.message.Regex;

public class Cookie {

    private static final int INDEX_KEY = 0;
    private static final int INDEX_VALUE = 1;
    private static final String KEY_JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    public Cookie(final Map<String, String> values) {
        this.values = values;
    }

    public static Cookie from(String rawCookies) {
        final Map<String, String> values = Arrays.stream(rawCookies.split(Regex.COOKIE_VALUE.getValue()))
                .map(rawCookie -> rawCookie.split(Regex.KEY_VALUE.getValue()))
                .collect(Collectors.toMap(
                        splitCookie -> splitCookie[INDEX_KEY],
                        splitCookie -> splitCookie[INDEX_VALUE]
                ));
        return new Cookie(values);
    }

    public static Cookie fromJSessionId(String sessionId) {
        return new Cookie(Map.of(KEY_JSESSIONID, sessionId));
    }

    public static Cookie ofEmpty() {
        return new Cookie(Map.of());
    }

    public Optional<String> getJSessionId() {
        return get(KEY_JSESSIONID);
    }

    public Optional<String> get(String key) {
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        }
        return Optional.empty();
    }

    public String toText() {
        return values.entrySet().stream()
                .map(entry -> String.join(Regex.KEY_VALUE.getValue(),
                        entry.getKey(), entry.getValue())
                ).collect(Collectors.joining(Regex.COOKIE_VALUE.getValue()));
    }
}
