package org.apache.coyote.http11.protocol.cookie;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int VALID_KEY_VALUE_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies = new LinkedHashMap<>();

    public Cookie() {
    }

    public Cookie(String cookieString) {
        if (cookieString == null) {
            return;
        }
        parseCookieString(cookieString);
    }

    private void parseCookieString(String cookieString) {
        Arrays.stream(cookieString.split(COOKIE_DELIMITER))
                .filter(cookiePair -> cookiePair.contains(KEY_VALUE_DELIMITER))
                .map(cookiePair -> cookiePair.split(KEY_VALUE_DELIMITER))
                .filter(keyValue -> keyValue.length == VALID_KEY_VALUE_LENGTH)
                .filter(keyValue -> !keyValue[KEY_INDEX].isEmpty())
                .forEach(keyValue -> cookies.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]));
    }

    public void setValue(String key, String value) {
        cookies.put(key, value);
    }

    public String getValue(String key) {
        return cookies.get(key);
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String toCookieString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}
