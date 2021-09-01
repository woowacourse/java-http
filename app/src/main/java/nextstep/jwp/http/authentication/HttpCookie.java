package nextstep.jwp.http.authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private static final String JSESSION_KEY = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_PAIR_DELIMITER = "=";
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(final String httpCookieValue) {
        if (httpCookieValue.isEmpty()) {
            return;
        }

        Arrays.stream(httpCookieValue.split(COOKIE_DELIMITER))
                .map(cookie -> cookie.split(COOKIE_KEY_PAIR_DELIMITER))
                .forEach(cookiePair -> cookies.put(cookiePair[0], cookiePair[1]));
    }

    public boolean doesNotHaveJSession() {
        return !cookies.containsKey(JSESSION_KEY);
    }
}
