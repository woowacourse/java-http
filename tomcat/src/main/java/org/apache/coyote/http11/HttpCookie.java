package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpCookie {

    private static final String LINE_REGEX = ";";
    private static final String KEY_AND_VALUE_REGEX = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public HttpCookie() {
        value = new HashMap<>();
    }

    public HttpCookie(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie from(final String line) {
        if (Objects.nonNull(line)) {
            final Map<String, String> cookie = new HashMap<>();
            final String[] keyAndValues = line.split(LINE_REGEX);
            for (String keyAndValue : keyAndValues) {
                final String[] split = keyAndValue.split(KEY_AND_VALUE_REGEX);
                final String key = split[KEY_INDEX];
                final String value = split[VALUE_INDEX];
                cookie.put(key, value);
            }
            return new HttpCookie(cookie);
        }
        return new HttpCookie(new HashMap<>());
    }

    public static String createJSessionId() {
        return UUID.randomUUID().toString();
    }

    public boolean containsSession() {
        return value.containsKey("JSESSIONID");
    }

    public String getSession() {
        return value.get("JSESSIONID");
    }
}
