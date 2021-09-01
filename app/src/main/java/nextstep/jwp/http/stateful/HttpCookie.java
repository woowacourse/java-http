package nextstep.jwp.http.stateful;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {

    public static final String JSESSION_ID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie: ";
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

    private Map<String, String> httpCookies = new HashMap<>();

    public HttpCookie(String cookieValue) {
        parseCookies(cookieValue);
    }

    private void parseCookies(String cookieValue) {
        if (cookieValue == null) {
            return;
        }

        List<String> splitCookies = Arrays.asList(cookieValue.split(COOKIE_SEPARATOR));

        splitCookies.forEach(it -> {
            String[] split = it.split(COOKIE_KEY_VALUE_SEPARATOR);
            httpCookies.put(split[0], split[1]);
        });
    }

    private String getCookieValue(String key) {
        return httpCookies.get(key);
    }

    public boolean containsKey(String key) {
        return httpCookies.containsKey(key);
    }

    public void setCookie(String key, String value) {
        httpCookies.put(key, value);
    }

    public boolean containSession() {
        return httpCookies.containsKey(JSESSION_ID);
    }

    public String getSessionId() {
        return httpCookies.get(JSESSION_ID);
    }
}
