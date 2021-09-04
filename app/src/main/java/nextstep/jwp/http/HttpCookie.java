package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String SPLIT_REGEX = "; ";
    private static final String KEY_VALUE_SPLIT = "=";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie create(String cookie) {
        Map<String, String> cookies = new HashMap<>();

        String[] splitCookies = cookie.split(SPLIT_REGEX);

        for (String splitCookie : splitCookies) {
            String[] keyValue = splitCookie.split(KEY_VALUE_SPLIT);
            cookies.put(keyValue[0], keyValue[1]);
        }

        return new HttpCookie(cookies);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
