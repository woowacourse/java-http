package nextstep.org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String SET_COOKIE_HEADER = "Set-Cookie: %s \r\n";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookie = new HashMap<>();

    public void parseCookieHeaders(String cookieHeaders) {
        Arrays.asList(cookieHeaders.split(COOKIE_DELIMITER)).forEach(header -> {
            String[] splited = header.split(KEY_VALUE_DELIMITER);
            cookie.put(splited[KEY_INDEX], splited[VALUE_INDEX]);
        });
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
        String cookies = cookie.entrySet().stream()
                .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));
        return String.format(SET_COOKIE_HEADER, cookies);
    }
}
