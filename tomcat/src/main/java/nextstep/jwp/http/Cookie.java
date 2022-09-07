package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cookie {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final String COOKIES_DELIMITER_REGEX = "; ";
    private static final String COOKIE_DELIMITER_REGEX = "=";
    private static final int COOKIE_DIVIDED_LIMIT = 2;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;
    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie parse(String cookieLine) {
        Map<String, String> cookies = new LinkedHashMap<>();
        List<String> dividedCookies = List.of(cookieLine.split(COOKIES_DELIMITER_REGEX));
        for (String cookie : dividedCookies) {
            List<String> dividedCookie = List.of(
                cookie.split(COOKIE_DELIMITER_REGEX, COOKIE_DIVIDED_LIMIT));
            cookies.put(dividedCookie.get(COOKIE_KEY_INDEX), dividedCookie.get(COOKIE_VALUE_INDEX));
        }
        return new Cookie(cookies);
    }

    public static Cookie fromJSessionId(String id) {
        return new Cookie(Map.of(JSESSIONID_KEY, id));
    }

    public static Cookie empty() {
        return new Cookie(Map.of());
    }

    public Optional<String> getJSessionId() {
        if (cookies.containsKey(JSESSIONID_KEY)) {
            return Optional.ofNullable(cookies.get(JSESSIONID_KEY));
        }
        return Optional.empty();
    }
}
