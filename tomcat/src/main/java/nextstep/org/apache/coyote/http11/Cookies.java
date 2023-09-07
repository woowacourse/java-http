package nextstep.org.apache.coyote.http11;

import static nextstep.org.apache.coyote.http11.HttpUtil.parseMultipleValues;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private static final String SET_COOKIE_HEADER = "Set-Cookie: %s \r\n";
    private static final String COOKIE_VALUES_DELIMITER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookie = new HashMap<>();

    public void parseCookieHeaders(String cookieHeaderValues) {
        parseMultipleValues(cookie,
                cookieHeaderValues, COOKIE_VALUES_DELIMITER, COOKIE_KEY_VALUE_DELIMITER);
    }

    public void set(String key, String value) {
        cookie.put(key, value);
    }

    public String get(String key) {
        return cookie.get(key);
    }

    public boolean isEmpty() {
        return cookie.isEmpty();
    }

    public boolean hasCookie(String key) {
        return cookie.containsKey(key);
    }

    public String createSetCookieHeader() {
        return cookie.entrySet().stream()
                .map(entry -> entry.getKey() + COOKIE_KEY_VALUE_DELIMITER + entry.getValue())
                .map(value -> String.format(SET_COOKIE_HEADER, value))
                .collect(Collectors.joining());
    }
}
