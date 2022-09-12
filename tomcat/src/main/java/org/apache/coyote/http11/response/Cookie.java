package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cookie {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_PARAMETER_DELIMITER = "=";
    private static final String COOKIE_CONNECTOR = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies = new HashMap<>();

    public static Cookie ofJSessionId(String id) {
        Cookie cookie = new Cookie();
        cookie.addCookie(JSESSIONID, id);
        return cookie;
    }

    public static Cookie of(String cookieValue) {
        Cookie cookie = new Cookie();
        String[] cookieValues = cookieValue.split(COOKIE_CONNECTOR);
        for (String value : cookieValues) {
            String[] contents = value.split(COOKIE_PARAMETER_DELIMITER);
            cookie.addCookie(contents[KEY_INDEX], contents[VALUE_INDEX]);
        }
        return cookie;
    }

    private void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public Optional<String> getJSessionValue() {
        if (cookies.containsKey(JSESSIONID)) {
            return Optional.of(cookies.get(JSESSIONID));
        }
        return Optional.empty();
    }

    public String parseToString() {
        return cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + COOKIE_PARAMETER_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIE_CONNECTOR));
    }
}
