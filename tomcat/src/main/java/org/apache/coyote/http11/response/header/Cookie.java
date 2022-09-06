package org.apache.coyote.http11.response.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class Cookie implements HttpResponseHeader {

    private static final String RESPONSE_HEADER_KEY = "Set-Cookie: ";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_NAME_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private static Cookie EMPTY = new Cookie(new HashMap<>());

    public static Cookie empty() {
        return EMPTY;
    }

    private final Map<String, String> values;

    public Cookie(Map<String, String> values) {
        this.values = values;
    }

    public static Cookie fromResponse(String cookieName, String cookieValue) {
        Map<String, String> generated = new ConcurrentHashMap<>();
        generated.put(cookieName, cookieValue);
        return new Cookie(generated);
    }

    public static Cookie fromRequest(String cookieHeaderValue) {
        String[] cookieValues = cookieHeaderValue.split(COOKIE_DELIMITER);
        Map<String, String> cookies = new ConcurrentHashMap<>();
        for (String cookie : cookieValues) {
            String[] cookieNameAndValue = cookie.split(COOKIE_KEY_VALUE_DELIMITER);
            cookies.put(cookieNameAndValue[COOKIE_NAME_INDEX], cookieNameAndValue[COOKIE_VALUE_INDEX]);
        }
        return new Cookie(cookies);
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

    public String getCookieOf(String cookieName) {
        return values.get(cookieName);
    }
}
