package nextstep.org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String SET_COOKIE_HEADER = "Set-Cookie: %s \r\n";
    public static final String COOKIE_DELIMITER = "; ";
    public static final String KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

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

    public String createSetCookieHeader() {
        String cookies = cookie.entrySet().stream()
                .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));
        return String.format(SET_COOKIE_HEADER, cookies);
    }
}
