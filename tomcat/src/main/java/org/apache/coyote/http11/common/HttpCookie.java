package org.apache.coyote.http11.common;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.util.ParamParser;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "=";
    private static final String COOKIES_DELIMITER = ";";

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String rawValue) {
        final Map<String, String> values = ParamParser.parseOf(rawValue, COOKIES_DELIMITER);

        return new HttpCookie(values);
    }

    public void add(final String key, final String value) {
        values.put(key, value);
    }

    public String get(final String key) {
        return values.get(key);
    }

    public String getAllCookies() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + COOKIE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIES_DELIMITER));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
