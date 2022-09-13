package session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieParser {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static boolean checkJSessionIdIsExistInCookieHeader(final String cookieHeaderValue) {
        return parseCookieHeader(cookieHeaderValue).stream()
                .map(Cookie::getKey)
                .anyMatch(i -> i.equals("JSESSIONID"));
    }

    public static List<Cookie> parseCookieHeader(final String cookieHeaderValue) {
        return Arrays.stream(cookieHeaderValue.split(COOKIE_DELIMITER))
                .map(i -> i.split(KEY_VALUE_DELIMITER))
                .map(i -> new Cookie(i[KEY_INDEX], i[VALUE_INDEX]))
                .collect(Collectors.toList());
    }

    public static String joinAllCookiesToString(final Map<String, Cookie> cookies) {
        return cookies.keySet().stream()
                .map(i -> toStringForHeader(cookies.get(i)))
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }

    public static String toStringForHeader(final Cookie cookie) {
        return cookie.getKey() + KEY_VALUE_DELIMITER + cookie.getValue();
    }
}
