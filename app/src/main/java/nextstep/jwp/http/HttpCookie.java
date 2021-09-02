package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie create(String cookie) {
        Map<String, String> cookies = new HashMap<>();

        String[] splitCookies = cookie.split("; ");

        for (String splitCookie : splitCookies) {
            String[] keyValue = splitCookie.split("=");
            cookies.put(keyValue[0], keyValue[1]);
        }

        return new HttpCookie(cookies);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
