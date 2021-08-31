package nextstep.jwp.http.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parse(String requestCookie) {
        String[] splitedCookies = requestCookie.split(";");

        Map<String, String> cookies = new HashMap<>();
        for (String cookie : splitedCookies) {
            String[] splitedCookie = cookie.trim().split("=");

            String cookieKey = splitedCookie[KEY_INDEX];
            String cookieValue = splitedCookie[VALUE_INDEX];

            cookies.put(cookieKey, cookieValue);
        }

        return new HttpCookie(cookies);
    }

    public void create() {
        UUID uuid = UUID.randomUUID();
        cookies.put("JSESSIONID", uuid.toString());
    }
}
