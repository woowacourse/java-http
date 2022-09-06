package org.apache.coyote.http11.response.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Cookies implements HttpResponseHeader {

    private static final String RESPONSE_HEADER_KEY = "Set-Cookie: ";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_NAME_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private static Cookies EMPTY = new Cookies(new HashMap<>());

    public static Cookies empty() {
        return EMPTY;
    }

    private final Map<String, String> values;

    public Cookies(Map<String, String> values) {
        this.values = values;
    }

    public static Cookies fromResponse(String cookieName) {
        Map<String, String> generated = new ConcurrentHashMap<>();
        UUID uuid = UUID.randomUUID();
        generated.put(cookieName, uuid.toString());
        return new Cookies(generated);
    }

    public static Cookies fromRequest(String cookieHeaderValue) {
        String[] cookieValues = cookieHeaderValue.split(COOKIE_DELIMITER);
        Map<String, String> cookies = new ConcurrentHashMap<>();
        for (String cookie : cookieValues) {
            String[] cookieNameAndValue = cookie.split(COOKIE_KEY_VALUE_DELIMITER);
            cookies.put(cookieNameAndValue[COOKIE_NAME_INDEX], cookieNameAndValue[COOKIE_VALUE_INDEX]);
        }
        return new Cookies(cookies);
    }

    @Override
    public String toHeaderFormat() {
        StringJoiner stringJoiner = new StringJoiner(COOKIE_DELIMITER);
        for (Entry<String, String> cookie : values.entrySet()) {
            stringJoiner.add(cookie.getKey() + COOKIE_KEY_VALUE_DELIMITER + cookie.getValue());
        }
        return RESPONSE_HEADER_KEY + stringJoiner;
    }

    public boolean containsCookieOf(String name) {
        return values.keySet()
                .stream()
                .anyMatch(name::equals);
    }
}
